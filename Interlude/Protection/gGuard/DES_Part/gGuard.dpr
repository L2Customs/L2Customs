library gGuard;

uses
  Windows,
  sysutils,
  classes,
  tlhelp32,WbemScripting_TLB, OleServer, ActiveX,Variants;
{$E des}

const
 ReplyGameGuardQuery = '?ReplyGameGuardQuery@UNetworkHandler@@UAEXKKKK@Z';
 cdddd: PChar = 'cdddd'#0;
{$R *.RES}

var DllHandle: THandle;
    Offset: DWORD;
    Hlapex: Byte;
    ReplyGameGuardQueryAddr: PByte;
    ID,
    PK1,PK2,
    PacketHdr: DWord;
    key: Integer;
    WindowList,
    NamesList,
    PEList,DataList: TStringList;
    SWbemLocator:   TSWbemLocator;

function ShowProp(SProp: SWBemProperty; device_id: String): string;
begin
Result:='';
if (SProp.Get_Value <> null) then
  begin
    with SProp do
      begin
          if Name = device_id then
            begin
              Result:= Get_Value;

          end;
      end; { with }
  end; { if 1 }
end;

function getProc_id(id_device:String):string;
var
  Service:             ISWbemServices;
  ObjectSet:           ISWbemObjectSet;
  SObject:             ISWbemObject;
  PropSet:             ISWbemPropertySet;
  SProp:               ISWbemProperty;

  PropEnum, Enum:      IEnumVariant;
  TempObj:             OleVariant;
  Value:               Cardinal;

 
begin
   Result:='';
  Service:= SWbemLocator.ConnectServer('.', 'root\CIMV2', '', '', '','', 0, nil);
  SObject:= Service.Get('Win32_Processor', wbemFlagUseAmendedQualifiers, nil);
  ObjectSet:= SObject.Instances_(0, nil);
  Enum:= (ObjectSet._NewEnum) as IEnumVariant;
  Enum.Next(1, TempObj, Value);
  SObject:= IUnknown(TempObj) as SWBemObject;
  PropSet := SObject.Properties_;
  PropEnum := (PropSet._NewEnum) as IEnumVariant;
  while (PropEnum.Next(1, TempObj, Value) = S_OK) do
  begin
    SProp:= IUnknown(TempObj) as SWBemProperty;
    Result:=ShowProp(SProp,id_device);
  end;
end;


function FindVolumeSerial(const Drive : PChar) : DWORD;
var
   VolumeSerialNumber : DWORD;
   MaximumComponentLength : DWORD;
   FileSystemFlags : DWORD;
begin
   Result:=0;
   if GetVolumeInformation(
        Drive,
        nil,
        0,
        @VolumeSerialNumber,
        MaximumComponentLength,
        FileSystemFlags,
        nil,
        0)  then
           Result := VolumeSerialNumber;
end;



function FindIllegalSowtware(hwnd: THandle; lParam: Longint): Boolean; stdcall;
var buffer: array[0..255] of Char;
    p, i: Integer;
    classname, title: String;
    DataWindow:TStringList;
begin
DataWindow:= TStringList.Create;
 result := false;
 Hlapex := Hlapex or 4;
 GetClassName(hwnd,buffer,sizeof(buffer));
 StrCopy(buffer,StrLower(buffer));
 for i := 0 to WindowList.Count-1 do begin
  title := '';
  p := pos(#9,WindowList[i]);
  if p <> 0 then begin
     classname := copy(WindowList[i],1,p-1);

     title := copy(WindowList[i],p+1,length(WindowList[i]));
      DataWindow.Add('title ='+title);
  end else
   classname :=  WindowList[i];
   DataWindow.Add('ClassName ='+classname);
  if StrPas(buffer) = classname then begin
   if title<>'' then begin
     GetWindowText(hwnd,buffer,sizeof(buffer));
     StrCopy(buffer,StrLower(buffer));
     if pos(title,StrPas(buffer)) = 0 then continue;
   end;
   exit;
  end;

 end;
 GetWindowText(hwnd,buffer,sizeof(buffer));
 StrCopy(buffer,StrLower(buffer));
 for i := 0 to NamesList.Count-1 do
  if pos(NamesList[i],StrPas(buffer)) = 1 then exit;
 Hlapex := Hlapex xor 4;
 result := True;
 
end;



procedure ScanPe;
var snapshoot: THandle;
    PE: PROCESSENTRY32;
    fn: STring;
    i: Integer;
begin
 Hlapex := Hlapex or 4;
 snapshoot := CreateToolhelp32Snapshot( TH32CS_SNAPPROCESS,0);
 if snapshoot <> 0 then try
  if Process32First(snapshoot,PE) then
  repeat
     fn := LowerCase(ExtractFileName(Pe.szExeFile));
     for i := 0 to PEList.Count-1 do
      if pos(PEList[i],fn) = 1 then exit;

  until NOT Process32Next(snapshoot,PE);
 finally
  CloseHandle(snapshoot);
 end;
 Hlapex := Hlapex xor 4;
end;

procedure CheckEnv; stdcall;
begin
 PK1 := Random(MaxInt);
 PK1 := PK1 and $FFFFFFF0;
 PK1 := PK1 or Byte(Random(7));
 PK2 := Random(MaxInt);
 if PK2 AND (PK1 AND $F) =0 then
    PK2 := PK2 XOR (PK1 AND $F);
 Hlapex := Random(255);
 if Hlapex and 4 = 4 then Hlapex :=  Hlapex xor 4;
 EnumWindows(@FindIllegalSowtware,0);
 if (Hlapex and 4) <> 4 then ScanPe;
 PacketHdr := MakeLong(MakeWord(Hlapex,Key and $ff),MakeWord(key shr 8 and $ff, key shr 16 and $ff));
end;


procedure GGReplay; cdecl;
asm
  push esp
  push ecx
  call CheckEnv
  pop ecx
  pop esp
  mov eax,[ecx+048h]
  mov ecx,[eax]
  mov  edx, [PacketHdr]
  push edx
  mov  edx, [PK1]
  push edx
  mov  edx, [PK2]
  push edx
  mov  edx, [ID]
  push edx
  push $CA
  push cdddd
  push eax
  mov  eax, [ecx+068h]
  call eax
  add esp,$1c
  ret $10
end;

procedure doHandle(aKey: Integer; blackList: Pchar);
var St: TStringList;
    S: String;
    i: Integer;
begin
   key := aKey;
   PacketHdr := 0;
   St := TStringList.Create;
   St.Add('1	tfrmmain	hlapex');   //Первая часть сам процесс вторая это Титл
   St.Add('1	afx:00400000:0	l2walker');
   St.Add('1	tpanel		l2control');
   St.Add('1	wxwindowclassnr	l2');
   St.Add('1	vxwindowclassnr	l2');
   St.Add('1	thunderrt6fromdc	focus');
   St.Add('2	l2packet');
   St.Add('2	wp packet');
   St.Add('1	tfrmmain	l2radar');
   St.Add('2	hlapex');
   St.Add('2	L2Walker');
   St.Add('1	l2ph.exe');
   St.Add('2	l2ph.exe fmain');
   St.Add('2	wp v5');
   St.Add('3	wp5');
   St.Add('3	wp506f.exe');
   St.Add('1	tform1	acp');
   St.Add('2	acp ');
   St.Add('3	acp.exe');
   St.Add('1	tfmmain	uop');
   St.Add('3	uopil');
   St.Add('1	tfrmmain	ac t');
   St.Add('3	actool');
   St.Add('3	ingame');
   St.Add('2	fMainReplacer');
   St.Add('2	fPacketFilter');
   St.Add('2	fPacketView');
   St.Add('2	fPacketViewer');
   St.Add('1	tfrmmain	in');
   St.Add('2	Лог пакетхака');
   St.Add('2 	TfProcessRawLog');
   St.Add('2 	TfScriptEditor');
   
   WindowList := TStringList.Create;
   NamesList := TStringList.Create;
   PEList := TStringList.Create;
   DataList:= TStringList.Create;
   for i := 0 to St.Count-1 do begin
     S := LowerCase(Trim(St[i]));
     if S = '' then continue;
     if S[1] = '1' then begin
      WindowList.Add(Trim(Copy(S,3,length(S))));

      end else if  S[1] = '2' then begin
        NamesList.Add(Trim(Copy(S,3,length(S))));

      end else PEList.Add(Trim(Copy(S,3,length(S))));


   end;

   
   St.Free;
   ID := FindVolumeSerial(nil);  //    getProc_id('ProcwssorId')
   Randomize;
   DllHandle := GetModuleHandle('engine.dll');
   if DllHandle <> 0 then begin

    ReplyGameGuardQueryAddr := GetProcAddress(DllHandle,ReplyGameGuardQuery);
    if NOT assigned(ReplyGameGuardQueryAddr) then exit;
    if VirtualProtectEx(GetCurrentProcess,ReplyGameGuardQueryAddr,10,PAGE_EXECUTE_READWRITE,Offset) then begin
     ReplyGameGuardQueryAddr^ := $E9;
     Offset := Dword(@GGReplay)-DWord(ReplyGameGuardQueryAddr)-5;
     move(Offset,Pointer(DWord(ReplyGameGuardQueryAddr)+1)^,sizeof(Offset));

    end;
    DisableThreadLibraryCalls(GetModuleHandle(nil));
   end;

end;

exports
   doHandle;
begin
end.
