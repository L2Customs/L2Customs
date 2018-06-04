#include <winsock2.h>
#include <windows.h>
#include <iphlpapi.h>
#include <stdio.h>
#include <time.h>
#include <tlhelp32.h>
#include <psapi.h>
#include <icmpapi.h>
#include <io.h>

#include "hook.h"
#include "blowfish.h"
#include "resource.h"
#include "Obfuscator.h"

#include "stdafx.h"
#include "ThemidaSDK.h"
#include "splash.h"

#pragma comment(lib, "User32.lib")
#pragma comment(lib, "ws2_32.lib")
#pragma comment(lib, "iphlpapi.lib")
#pragma comment(lib, "psapi.lib")

struct TFontDrawInfo
{
	int font;
	unsigned int color, u1, u2, u3, u4;
};

struct TNetworkPacket
{
    unsigned char id, p1;
    unsigned short int subid, size, p2;
    unsigned char *data;
};

struct TSpecialString
{
	bool isDraw;
	TFontDrawInfo FontDrawInfo;
	int x, y;
	wchar_t text[64];
};

#pragma pack(1)
typedef struct _IDENTIFY_DATA
{
	USHORT GeneralConfiguration;
    USHORT NumberOfCylinders;
    USHORT Reserved1;
    USHORT NumberOfHeads;
    USHORT UnformattedBytesPerTrack;
    USHORT UnformattedBytesPerSector;
    USHORT SectorsPerTrack;
    USHORT VendorUnique1[3];
    USHORT SerialNumber[10];
    USHORT BufferType;
    USHORT BufferSectorSize;
    USHORT NumberOfEccBytes;
    USHORT FirmwareRevision[4];
    USHORT ModelNumber[20];
    UCHAR  MaximumBlockTransfer;
    UCHAR  VendorUnique2;
    USHORT DoubleWordIo;
    USHORT Capabilities;
    USHORT Reserved2;
    UCHAR  VendorUnique3;
    UCHAR  PioCycleTimingMode;
    UCHAR  VendorUnique4;
    UCHAR  DmaCycleTimingMode;
    USHORT TranslationFieldsValid:1;
    USHORT Reserved3:15;
    USHORT NumberOfCurrentCylinders;
    USHORT NumberOfCurrentHeads;
    USHORT CurrentSectorsPerTrack;
    ULONG  CurrentSectorCapacity;
    USHORT CurrentMultiSectorSetting;
    ULONG  UserAddressableSectors;
    USHORT SingleWordDMASupport : 8;
    USHORT SingleWordDMAActive : 8;
    USHORT MultiWordDMASupport : 8;
    USHORT MultiWordDMAActive : 8;
    USHORT AdvancedPIOModes : 8;
    USHORT Reserved4 : 8;
    USHORT MinimumMWXferCycleTime;
    USHORT RecommendedMWXferCycleTime;
    USHORT MinimumPIOCycleTime;
    USHORT MinimumPIOCycleTimeIORDY;
    USHORT Reserved5[2];
    USHORT ReleaseTimeOverlapped;
    USHORT ReleaseTimeServiceCommand;
    USHORT MajorRevision;
    USHORT MinorRevision;
    USHORT Reserved6[50];
    USHORT SpecialFunctionsEnabled;
    USHORT Reserved7[128];
} IDENTIFY_DATA, *PIDENTIFY_DATA;
#pragma pack()

typedef int (__stdcall *_connect) (SOCKET s, const struct sockaddr *name, int namelen);
_connect true_connect;

typedef int (__fastcall *_AddNetworkQueue) (unsigned int This, unsigned int EDX, TNetworkPacket *NetworkPacket);
_AddNetworkQueue true_AddNetworkQueue;

typedef void (__cdecl *_SendPacket) (unsigned int This, char *Format, ...);
_SendPacket true_SendPacket;

typedef void (_fastcall *_MasterProcessPreRender) (unsigned int This, unsigned int EDX, unsigned int UCanvas);
_MasterProcessPreRender true_MasterProcessPreRender;

typedef int (_fastcall *_DrawTextTTFToCanvas) (unsigned int This, unsigned int EDX, int X, int Y, wchar_t *text, TFontDrawInfo *FontDrawInfo, unsigned char, int, int, unsigned int FontDrawInfoSection);
_DrawTextTTFToCanvas true_DrawTextTTFToCanvas;

typedef void (_fastcall *_Render) (unsigned int This, unsigned int EDX, unsigned int FRenderInterface);
_Render true_Render;

RECT rc;
HWND hWND;
TSpecialString SpecialStrings[16];
unsigned int Canvas, hEngineStart, hEngineEnd, mainThread, lastPing = 0, KiUserExceptDispADR, hNtDllStart, hNtDllEnd;
HANDLE ghMutex;

void ErrorExit(char *msg)
{
	FILE *f;
	
	fopen_s(&f, "Protection.log", "a+");

	if (f != 0)
	{
		fprintf(f, "%s\n", msg);
		fclose(f);
	}

	ExitProcess(0);
}

void Logger(char *msg)
{
	FILE *f;
	
	fopen_s(&f, "Debug.log", "a+");

	if (f != 0)
	{
		fprintf(f, "%s\n", msg);
		fclose(f);
	}
}

bool SetPrivileges(HANDLE hProcess, DWORD dwPrivilegeCount, LPCTSTR *ppPrivilegeName)
{
	HANDLE hToken = 0;
	if(!::OpenProcessToken(hProcess, TOKEN_ALL_ACCESS, &hToken))
		return false;

	PTOKEN_PRIVILEGES Privileges = (PTOKEN_PRIVILEGES)
		new char[sizeof(DWORD) + sizeof(LUID_AND_ATTRIBUTES) * dwPrivilegeCount];

	LUID_AND_ATTRIBUTES *LuidAndAttr = (LUID_AND_ATTRIBUTES *)(((char *)Privileges) + sizeof(DWORD));

	Privileges->PrivilegeCount = dwPrivilegeCount;

	for(DWORD i = 0; i < dwPrivilegeCount; i++)
	{
		if(!::LookupPrivilegeValue(0, 
			ppPrivilegeName[i], 
			&LuidAndAttr[i].Luid))
		{
			::CloseHandle(hToken);
			delete[]Privileges;
			return false; 
		}

		LuidAndAttr[i].Attributes = SE_PRIVILEGE_ENABLED;
	}
	if(!::AdjustTokenPrivileges(hToken, FALSE, Privileges, 0, 0, 0))
	{
		::CloseHandle(hToken);
		delete[]Privileges;
		return false;
	}
	::CloseHandle(hToken);
	delete[]Privileges;
	return true;
}

bool checkRun(char* NAmePorc)
{
    HANDLE hProcessSnap = NULL; 
    PROCESSENTRY32 pe32;
    hProcessSnap = CreateToolhelp32Snapshot(TH32CS_SNAPPROCESS, 0); 
    if(hProcessSnap == INVALID_HANDLE_VALUE) 
	{
		return true;
	}
    pe32.dwSize = sizeof(PROCESSENTRY32); 
    if(Process32First(hProcessSnap, &pe32)) 
    { 
        while(Process32Next(hProcessSnap, &pe32));
		{
			if(strstr(NAmePorc, (char*)pe32.szExeFile))
			{
				CloseHandle (hProcessSnap);
				return true;
			}
		}
    }
    CloseHandle(hProcessSnap);
    return false;
}

int __stdcall new_connect(SOCKET s, const struct sockaddr *name, int namelen)
{
	struct sockaddr_in *name_in = (struct sockaddr_in*) name;
	if (name_in->sin_port == htons(2106))
	{
		name_in->sin_port = htons(2106);
		name_in->sin_addr.S_un.S_addr = inet_addr("192.168.1.7");
	}

	if (name_in->sin_port == htons(7777))
	{
		name_in->sin_port = htons(7777);
		name_in->sin_addr.S_un.S_addr = inet_addr("192.168.1.7");
	}
	return true_connect(s, name, namelen);
}

void decodeKey(unsigned char *buf)
{
	unsigned char bfkey[16] =  {110, 36, 2, 15, -5, 17, 24, 23, 18, 45, 1, 21, 122, 16, -5, 12}; //STATIC KEY
	unsigned char tmp[24];
	Blowfish_CTX ctx;

	memcpy(tmp, buf, 16);

	Blowfish_Init(&ctx, bfkey, 16);
	Blowfish_Decrypt(&ctx, tmp, 16);

	memcpy(buf, tmp, 16);
}

int __fastcall new_AddNetworkQueue(unsigned int This, unsigned int EDX, TNetworkPacket *NetworkPacket)
{
	if ((*(unsigned int*) (KiUserExceptDispADR + 10) + KiUserExceptDispADR < hNtDllStart) || (*(unsigned int*) (KiUserExceptDispADR + 10) + KiUserExceptDispADR > hNtDllEnd))
		ErrorExit("Bot Program Detected #0!");

	unsigned int retAddr = *((unsigned int*) &NetworkPacket - 1);

	if ((retAddr < hEngineStart) || (retAddr > hEngineEnd))
		ErrorExit("Bot Program Detected #1!");

	if (NetworkPacket->subid == 0xFFFF)
	{
		switch (NetworkPacket->id)
		{
			case 0x2E:
				decodeKey(NetworkPacket->data + 1);
				break;
			case 0xB0:
				memset(&SpecialStrings[NetworkPacket->data[0]].FontDrawInfo, 0, sizeof(TFontDrawInfo));
				SpecialStrings[NetworkPacket->data[0]].isDraw = (NetworkPacket->data[1] == 1) ? true : false;
				SpecialStrings[NetworkPacket->data[0]].FontDrawInfo.font = (char) NetworkPacket->data[2];
				SpecialStrings[NetworkPacket->data[0]].x = *(int*) (NetworkPacket->data + 3);
				SpecialStrings[NetworkPacket->data[0]].y = *(int*) (NetworkPacket->data + 7);
				SpecialStrings[NetworkPacket->data[0]].FontDrawInfo.color = *(unsigned int*) (NetworkPacket->data + 11);
				wcscpy_s(SpecialStrings[NetworkPacket->data[0]].text, (wchar_t*) (NetworkPacket->data + 15));
				if (NetworkPacket->data[0] == 14)
				{
					HANDLE hIcmpFile = IcmpCreateFile();
					ICMP_ECHO_REPLY Reply;
					if (hIcmpFile != 0)
					{
						if (IcmpSendEcho(hIcmpFile, inet_addr("192.168.1.7"), 0, 0, 0, &Reply, sizeof(ICMP_ECHO_REPLY), 500) != 0)
							lastPing = Reply.RoundTripTime;
						IcmpCloseHandle(hIcmpFile);
					}
					wsprintf(SpecialStrings[NetworkPacket->data[0]].text, L"%ls%d", (wchar_t*) (NetworkPacket->data + 15), lastPing);
				}
				break;
		}
	}

	return true_AddNetworkQueue(This, EDX, NetworkPacket);
}

bool getHWID(wchar_t *str)
{
	HW_PROFILE_INFO   HwProfInfo;
	char HWID[1024];

	if (!GetCurrentHwProfile(&HwProfInfo)) 
	{
		return false;
	}
	sprintf_s(HWID, 1000, "%02X%02X%02X%02X", HwProfInfo.szHwProfileGuid);
	mbstowcs_s(0, str, 1000, HWID, 1000);

	return true;
}

bool getMAC(wchar_t *str)
{
	IP_ADAPTER_INFO AdapterInfo[16];
	PIP_ADAPTER_INFO pAdapterInfo;
	unsigned long dwBufLen;
	char MAC[1024];

	dwBufLen = sizeof(AdapterInfo);

	if (GetAdaptersInfo(AdapterInfo, &dwBufLen) != ERROR_SUCCESS)
		return false;

	pAdapterInfo = AdapterInfo;

	while ((pAdapterInfo->Address[0] == 0) && (pAdapterInfo->Address[1] == 0) && (pAdapterInfo->Address[2] == 0) &&(pAdapterInfo->Address[3] == 0) && (pAdapterInfo->Address[4] == 0) && (pAdapterInfo->Address[5] == 0))
		pAdapterInfo = pAdapterInfo->Next;

	sprintf_s(MAC, 1000, "%02X%02X%02X%02X%02X%02X", pAdapterInfo->Address[0], pAdapterInfo->Address[1], pAdapterInfo->Address[2], pAdapterInfo->Address[3], pAdapterInfo->Address[4], pAdapterInfo->Address[5]);

	mbstowcs_s(0, str, 1000, MAC, 1000);

	return true;
}

char *flipAndCodeBytes (const char *str, int pos, int flip, char *buf)
{
	int i;
	int j = 0;
	int k = 0;

	buf[0] = '\0';

	if (pos <= 0)
		return buf;

	if (!j)
	{
		char p = 0;

		j = 1;
		k = 0;
		buf[k] = 0;

		for (i = pos; j && str[i] != '\0'; ++i)
		{
			char c = tolower(str[i]);

			if (isspace(c))
				c = '0';

			++p;
			buf[k] <<= 4;

			if (c >= '0' && c <= '9')
				buf[k] |= (unsigned char) (c - '0');
			else if (c >= 'a' && c <= 'f')
				buf[k] |= (unsigned char) (c - 'a' + 10);
			else
			{
				j = 0;
				break;
			}

			if (p == 2)
			{
				if (buf[k] != '\0' && ! isprint(buf[k]))
				{
					j = 0;
					break;
				}

				++k;
				p = 0;
				buf[k] = 0;
			}
		}
	}

	if (!j)
	{
		j = 1;
		k = 0;

		for (i = pos; j && str[i] != '\0'; ++i)
		{
			char c = str[i];

			if ( ! isprint(c))
			{
				j = 0;
				break;
			}

			buf[k++] = c;
		}
	}

	if (!j)
	{
		k = 0;
	}

	buf[k] = '\0';

	if (flip)
		for (j = 0; j < k; j += 2)
		{
			char t = buf[j];
			buf[j] = buf[j + 1];
			buf[j + 1] = t;
		}

		i = j = -1;

		for (k = 0; buf[k] != '\0'; ++k)
		{
			if (! isspace(buf[k]))
			{
				if (i < 0)
					i = k;

				j = k;
			}
		}

		if ((i >= 0) && (j >= 0))
		{
			for (k = i; (k <= j) && (buf[k] != '\0'); ++k)
				buf[k - i] = buf[k];

			buf[k - i] = '\0';
		}

		return buf;
}

bool getHDDSerialNumber(wchar_t *str)
{
	HANDLE hPhysicalDriveIOCTL = 0;
	char serialNumber[1024], windir[256], filename[256];

	if (GetWindowsDirectoryA(windir, 256) == 0)
		return false;

	memset(filename, 0, 256);

	strcpy_s(filename, "\\\\.\\\\");
	windir[2] = 0;
	strcat_s(filename, windir);

	hPhysicalDriveIOCTL = CreateFileA(filename, 0, FILE_SHARE_READ | FILE_SHARE_WRITE, 0, OPEN_EXISTING, 0, 0);

	if (hPhysicalDriveIOCTL == INVALID_HANDLE_VALUE)
		return false;

	STORAGE_PROPERTY_QUERY query;
	unsigned long cbBytesReturned = 0;
	char buffer[10000];

	memset ((void *) &query, 0, sizeof (query));
	query.PropertyId = StorageDeviceProperty;
	query.QueryType = PropertyStandardQuery;

	memset (buffer, 0, sizeof (buffer));

	if (!(DeviceIoControl(hPhysicalDriveIOCTL, IOCTL_STORAGE_QUERY_PROPERTY, &query, sizeof(query), &buffer, sizeof(buffer), &cbBytesReturned, 0)))
	{
		CloseHandle(hPhysicalDriveIOCTL);
		return false;
	}

	STORAGE_DEVICE_DESCRIPTOR * descrip = (STORAGE_DEVICE_DESCRIPTOR *) & buffer;

	sprintf_s(serialNumber, 1000, "%02X%02X", flipAndCodeBytes(buffer, descrip->SerialNumberOffset, 1, serialNumber));

	mbstowcs_s(0, str, 1000, serialNumber, 1000);

	return true;
}

bool getHwGuid(wchar_t *str)
{
	HW_PROFILE_INFOA HwProfInfo;
	char HWID[1024];

	if (GetCurrentHwProfileA(&HwProfInfo) == 0)
		return false;

	sprintf_s(HWID, 1000, "%02X%02X", HwProfInfo.szHwProfileGuid);

	mbstowcs_s(0, str, 1000, HWID, 1000);

	return true;
}

void __cdecl new_SendPacket(unsigned int This, char *Format, ...)
{
	if ((*(unsigned int*) (KiUserExceptDispADR + 10) + KiUserExceptDispADR < hNtDllStart) || (*(unsigned int*) (KiUserExceptDispADR + 10) + KiUserExceptDispADR > hNtDllEnd))
		ErrorExit("Bot Program Detected #2!");

	unsigned int retAddr = *((unsigned int*) &This - 1);

	if ((retAddr < hEngineStart) || (retAddr > hEngineEnd))
	{
		true_SendPacket(This, "cc", 0xA1, 0x01);
		ErrorExit("Bot Program Detected #3!");
	}

	if (((unsigned int) Format < hEngineStart) || ( (unsigned int) Format > hEngineEnd))
	{
		true_SendPacket(This, "cc", 0xA1, 0x02);
		ErrorExit("Bot Program Detected #4!");
	}

	if (GetCurrentThreadId() != mainThread)
	{
		true_SendPacket(This, "cc", 0xA1, 0x03);
		ErrorExit("Bot Program Detected #5!");
	}
	
	unsigned char buf[10240];
	int size = 0, len;
	wchar_t *wstr;

    va_list args;
	va_start(args, Format);

	while (*Format != 0)
	{
		switch (*Format)
		{
			case 'c':
				*(unsigned char*) (buf + size) = va_arg(args, unsigned char);
				size++;
				break;
			case 'h':
				*(unsigned short int*) (buf + size) = va_arg(args, unsigned short int);
				size += 2;
				break;
			case 'd':
				*(unsigned int*) (buf + size) = va_arg(args, unsigned int);
				size += 4;
				break;
			case 'Q':
				*(unsigned __int64*) (buf + size) = va_arg(args, unsigned __int64);
				size += 8;
				break;
			case 'b':
				len = va_arg(args, unsigned int);
				memcpy(buf + size, va_arg(args, void*), len);
				size += len;
				break;			
			case 'S':
				wstr = va_arg(args, wchar_t*);
				if (wstr == 0)
				{
					len = 2;
					*(unsigned short int*) (buf + size) = 0;
				}
				else
				{
					len = wcslen(wstr) * 2 + 2;
					memcpy(buf + size, wstr, len);
				}
				size += len;
				break;
			default:
				true_SendPacket(This, "cc", 0xA1, 0x04);
				ErrorExit("Send Packet Unknown Format!");
				break;
		}
		Format++;	
	}

	va_end(args);

	switch(buf[0])
	{
		case 0x0E:
			wchar_t serialNumber[1024], MAC[1024], HwGuid[1024];
			memset(serialNumber, 0, 1024 * 2);
			memset(MAC, 0, 1024 * 2);
			if (!getHDDSerialNumber(serialNumber))
			{
				true_SendPacket(This, "cc", 0xA1, 0x05);
				ErrorExit("Get HDD Serial Number Fail!");
				return;
			}
			if (!getMAC(MAC))
			{
				true_SendPacket(This, "cc", 0xA1, 0x06);
				ErrorExit("Get MAC Fail!");
				return;
			}
			if (!getHwGuid(HwGuid))
			{
				true_SendPacket(This, "cc", 0xA1, 0x07);
				ErrorExit("Get HWID Fail!\n");
			}
			if ((wcslen(MAC) == 0) || (wcslen(serialNumber) == 0) || (wcslen(HwGuid) == 0))
			{
				true_SendPacket(This, "cc", 0xA1, 0x08);
				ErrorExit("Wrong MAC / Serial Number / HWID!");
				return;
			}

			memcpy(buf + size, serialNumber, wcslen(serialNumber) * 2 + 2);
			size += wcslen(serialNumber) * 2 + 2;
			memcpy(buf + size, MAC, wcslen(MAC) * 2 + 2);
			size += wcslen(MAC) * 2 + 2;
			memcpy(buf + size, HwGuid, wcslen(HwGuid) * 2 + 2);
			size += wcslen(HwGuid) * 2 + 2;
			break;
	}
	true_SendPacket(This, "b", size, (int)buf);
}


void _fastcall new_Render(unsigned int This, unsigned int EDX, unsigned int FRenderInterface)
{
	mainThread = GetCurrentThreadId();

	RECT L2Rect;
	HWND *L2hWND = (HWND*) GetProcAddress(LoadLibraryA("core.dll"), "?GTopWnd@@3PAUHWND__@@A");

	true_Render(This, EDX, FRenderInterface);

	GetClientRect(*L2hWND, &L2Rect);

	for (int i = 0; i < 16; i++)
	{
		if (SpecialStrings[i].isDraw)
		{
			if (i == 15)
			{
				time_t rawtime;
				struct tm timeinfo;
				wchar_t exstr[16], newstr[64];

				time(&rawtime);
				localtime_s(&timeinfo, &rawtime);
				wcsftime(exstr, 15, L"%H:%M", &timeinfo);
				wcscpy_s(newstr, SpecialStrings[i].text);
				wcscat_s(newstr, exstr);

				true_DrawTextTTFToCanvas(Canvas, 0, L2Rect.right - SpecialStrings[i].x, L2Rect.top + SpecialStrings[i].y, newstr, &SpecialStrings[i].FontDrawInfo, 0xFF, 0, 0, 0);
			}
			else
				true_DrawTextTTFToCanvas(Canvas, 0, L2Rect.right - SpecialStrings[i].x, L2Rect.top + SpecialStrings[i].y, SpecialStrings[i].text, &SpecialStrings[i].FontDrawInfo, 0xFF, 0, 0, 0);
		}
	}
}

void _fastcall new_MasterProcessPreRender(unsigned int This, unsigned int EDX, unsigned int UCanvas)
{
	Canvas = UCanvas;

	true_MasterProcessPreRender(This, EDX, UCanvas);
}

bool DisableGameGuard(void)
{
	unsigned long oldProtect;
	unsigned char *GL2UseGameGuard;

	GL2UseGameGuard = (unsigned char *) GetProcAddress(LoadLibraryA("core.dll"), "?GL2UseGameGuard@@3HA");

	if(GL2UseGameGuard == 0)
		return false;
	
	if(!VirtualProtect(GL2UseGameGuard, 1, PAGE_READWRITE, &oldProtect))
		return false;

	*GL2UseGameGuard = 0;

	if(!VirtualProtect(GL2UseGameGuard, 1, oldProtect, &oldProtect))
		return false;

	return true;
}

unsigned int GetSendPacketAddress(void)
{
	HMODULE hEngine = LoadLibraryA("engine.dll");

	unsigned int startVMT = (unsigned int) hEngine + 0x51F658;
	unsigned int AddNetworkQueue = (unsigned int) GetProcAddress(hEngine, "?AddNetworkQueue@UNetworkHandler@@UAEHPAUNetworkPacket@@@Z");
	unsigned int currVMT = startVMT;

	if (AddNetworkQueue == 0)
	{
		return 0;
	}

	while (true)
	{
		if (*(unsigned int*) currVMT == AddNetworkQueue)
		{
			return *(unsigned int*) (currVMT - 0xA4);
		}

		currVMT++;
		if (currVMT - startVMT > 10000)
		{
			return 0;
		}
	}
	return 0;
}

void TitleReplaceHookThread(void *param)
{
	while(true)
	{
		Sleep(50);
		HWND hWnd = FindWindow(NULL, L"Lineage II");
		if(hWnd)
		{
			SetWindowText(hWnd, L" ");
			break;
		}
	}
}

void TitleReplaceHook()
{
	_beginthread(TitleReplaceHookThread, 0, NULL);
	CloseHandle(TitleReplaceHookThread);
}

bool SetHooks(void)
{
	FARPROC addr;
	HMODULE hEngine = LoadLibraryA("engine.dll"), hNtDlll = LoadLibraryA("ntdll.dll");
	MODULEINFO modinfo;

	GetModuleInformation(GetCurrentProcess(), hEngine, &modinfo, sizeof(MODULEINFO));

	hEngineStart = (unsigned int) hEngine;
	hEngineEnd = hEngineStart + modinfo.SizeOfImage - 1;

	GetModuleInformation(GetCurrentProcess(), hNtDlll, &modinfo, sizeof(MODULEINFO));

	hNtDllStart = (unsigned int) hNtDlll;
	hNtDllEnd = hNtDllStart + modinfo.SizeOfImage - 1;

	if ((KiUserExceptDispADR = (unsigned int) GetProcAddress(LoadLibraryA("ntdll.dll"), "KiUserExceptionDispatcher")) == 0)
		return false;

	if (*(unsigned char*) (KiUserExceptDispADR) == 0xFC)
		KiUserExceptDispADR++;

	if ((addr = GetProcAddress(LoadLibraryA("ws2_32.dll"), "connect")) == 0)
		return false;

	true_connect = (_connect) splice((unsigned char*) addr, new_connect);

	if ((addr = GetProcAddress(hEngine, "?AddNetworkQueue@UNetworkHandler@@UAEHPAUNetworkPacket@@@Z")) == 0)
		return false;

	true_AddNetworkQueue = (_AddNetworkQueue) splice((unsigned char*) addr, new_AddNetworkQueue);

	if ((addr = (FARPROC) GetSendPacketAddress()) == 0)
		return false;

	true_SendPacket = (_SendPacket) splice((unsigned char*) addr, new_SendPacket);

	if ((addr = GetProcAddress(hEngine, "?MasterProcessPreRender@UInteractionMaster@@QAEXPAVUCanvas@@@Z")) == 0)
		return false;

	true_MasterProcessPreRender = (_MasterProcessPreRender) splice((unsigned char*) addr, new_MasterProcessPreRender);

	if ((addr = GetProcAddress(hEngine, "?DrawTextTTFToCanvas@UCanvas@@QAEHHHPB_WPBVFontDrawInfo@@EHHPBV?$TArray@PAVFontDrawInfoSection@@@@@Z")) == 0)
		return false;

	true_DrawTextTTFToCanvas = (_DrawTextTTFToCanvas) addr;

	if ((addr = GetProcAddress(hEngine, "?Render@FPlayerSceneNode@@UAEXPAVFRenderInterface@@@Z")) == 0)
		return false;

	true_Render = (_Render) splice((unsigned char*) addr, new_Render);

	TitleReplaceHook();
	return true;
}

LRESULT CALLBACK WndProc(HWND hWnd, UINT uMsg, WPARAM wParam, LPARAM lParam)
{
	return DefWindowProc(hWnd, uMsg, wParam, lParam);
}

DWORD GetCurrentIP()
{
	WSADATA wsaData;
	WSAStartup(MAKEWORD(1,1), &wsaData);

	char HostName[1024];
	DWORD m_HostIP = 0;

	if(!gethostname(HostName, 1024))
	{
		if(LPHOSTENT lphost = gethostbyname(HostName))
			m_HostIP = ((LPIN_ADDR)lphost->h_addr)->s_addr;
	}
	WSACleanup();
	return m_HostIP;
}
bool LoadTrayIcon(HINSTANCE hInst, unsigned int ID)
{
	ghMutex = CreateMutexA(0, false, "Protected on "+GetCurrentIP());

	if (GetLastError() == ERROR_ALREADY_EXISTS)
	{
		CloseHandle(ghMutex);
		return true;
	}

	WNDCLASSEXA wcx;
	NOTIFYICONDATAA niData; 

	memset(&wcx, 0, sizeof(WNDCLASSEXA));

	wcx.cbSize = sizeof(wcx);
	wcx.lpfnWndProc = WndProc;
	wcx.hInstance = hInst;
	wcx.lpszClassName = "tray_icon";

	if ((RegisterClassExA(&wcx) == 0) && (GetLastError() != ERROR_CLASS_ALREADY_EXISTS))
		return false;

	if ((hWND = CreateWindowExA(0, "tray_icon", 0, 0, 0, 0, 0, 0, 0, 0, hInst, 0)) == 0)
		return false;

	memset(&niData, 0, sizeof(NOTIFYICONDATAA));

	niData.cbSize = sizeof(NOTIFYICONDATAA);
	niData.uID = ID;
	niData.uFlags = NIF_ICON | NIF_TIP;
	niData.hWnd = hWND;
	strcpy_s(niData.szTip, "Protected on "+GetCurrentIP());

	niData.hIcon = (HICON) LoadImageA(hInst, MAKEINTRESOURCEA(IDI_TRAYICON), IMAGE_ICON, GetSystemMetrics(SM_CXSMICON), GetSystemMetrics(SM_CYSMICON), LR_DEFAULTCOLOR);

	if (!Shell_NotifyIconA(NIM_ADD, &niData))
		return false;

	return true;
}

void FreeTrayIcon(unsigned int ID)
{
	NOTIFYICONDATAA niData; 

	memset(&niData, 0, sizeof(NOTIFYICONDATAA));
	niData.cbSize = sizeof(NOTIFYICONDATAA);
	niData.hWnd = hWND;
	niData.uID = ID;

	Shell_NotifyIconA(NIM_DELETE, &niData);

	CloseHandle(ghMutex);
}

int DirectXSetupGetVersion(void)
{
	return 0;
}

void InitConsole(void)
{
	AllocConsole();

	stdout->_file = _open_osfhandle((intptr_t) GetStdHandle(STD_OUTPUT_HANDLE), 0);
	stdin->_file = _open_osfhandle((intptr_t) GetStdHandle(STD_INPUT_HANDLE), 0);
	stderr->_file = _open_osfhandle((intptr_t) GetStdHandle(STD_ERROR_HANDLE), 0);
}

BOOL WINAPI DllMain(HINSTANCE hinstDLL, DWORD fdwReason, LPVOID lpvReserved)
{
	if (fdwReason == DLL_PROCESS_ATTACH)
	{
		HBITMAP hBmp = LoadBitmap(hinstDLL,MAKEINTRESOURCE(102)); //Load bitmap
		if(hBmp)
		{
			CSplash splash; //Init class
			splash.SetBitmap(hBmp); //Set image(BMP + alpha)
			splash.SetTransparentColor(RGB(255, 255, 255)); //Select color for mask
			splash.ShowSplash(); //Show splash window and image
			Sleep(2000); //Sleep for freeze window
			splash.CloseSplash(); //Close class handler
		}

		DisableThreadLibraryCalls(hinstDLL);

		LoadTrayIcon(hinstDLL, 1);

		if (!DisableGameGuard())
		{
			ErrorExit("Game Guard is not allow this client!");
			Sleep(INFINITE);
		}

		if (!SetHooks())
		{
			ErrorExit("Protection is not attached this client!");
			Sleep(INFINITE);
		}
	}

	if (fdwReason == DLL_PROCESS_DETACH)
	{
		FreeTrayIcon(1);
	}

	return true;
}