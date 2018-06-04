#include <iostream>

using namespace std;

#pragma warning(disable: 4035)

bool CPUID_supported(void)
{
	__asm
	{
		pushfd
		pop eax
		mov ebx, eax
		xor eax, 0x00200000
		push eax
		popfd
		pushfd
		pop eax
		cmp eax, ebx
		jz NO_CPUID
		mov eax, 1
		jmp RETURN_CPUID_SUPPORTED
	}

	NO_CPUID:
	__asm mov eax, 0
	RETURN_CPUID_SUPPORTED:
	;
}

__declspec( naked )

unsigned long __cdecl CPU_Vendor(char *s)
{
	__asm
	{
		push	edi

		mov	eax, 0
		cpuid
		mov	edi, [esp+8]
		mov	[edi], ebx
		mov	[edi+4], edx
		mov	[edi+8], ecx
		mov	byte ptr [edi+12], 0

		pop	edi
		ret
	}
}

unsigned long __cdecl CPUID_Features(void)
{
	__asm
	{
		mov eax, 1
		cpuid
		mov eax, edx
	}
}

int main(void)
{
	if(!CPUID_supported())
	{
		cout << "Sorry, it seems that CPU doesn't support CPUID instruction\n";
		return(-1);
	}

	char vendor[13];		// 3 dwords + \0
	unsigned long max_CPUID = CPU_Vendor(vendor);

	if(max_CPUID==0)
		cout << endl << "Sorry, but CPU don't support CPUID request, eax=1\n";
	else
	{
		unsigned long id = CPUID_Features();
		cout << "CPUID_Features=0x"  << hex << id << dec << endl << "---------------------------------\n";
	}

	return(0);
}