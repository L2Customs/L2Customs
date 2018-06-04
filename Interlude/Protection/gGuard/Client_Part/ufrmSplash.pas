unit ufrmSplash;

interface

uses
  Windows, Messages, SysUtils, Classes, Graphics, Controls, Forms, Dialogs,
  ExtCtrls, StdCtrls, ComCtrls,httpsend, jpeg;

const WM_LOAD = WM_USER+1001;
type
  TfrmSplash = class(TForm)
    Image1: TImage;
    Label1: TLabel;
    Memo1: TMemo;
    procedure FormShow(Sender: TObject);
    procedure FormClose(Sender: TObject; var Action: TCloseAction);
  private
    procedure doLoad(var Msg: TMessage); message WM_LOAD;



    procedure ErrMsg(Msg: String);
    { Private declarations }
  public

   { Public declarations }
  end;


Type
 TdoHandle = procedure(aKey: Integer; blackList: Pchar);

 var
  frmSplash: TfrmSplash;




implementation
uses Hash,base64,DECUtil,JclMime,CONVUNIT;
{$R *.DFM}

function DecodeStream(Stream: TStream): String;
var
 p,Buff: Pchar;
 i: Integer;
 XorKey: Byte;
begin
 Buff := AllocMem(Stream.Size);
 fillchar(Buff^,Stream.Size,0);
 Stream.Read(Buff^,Stream.Size);
 if Buff[0] = 'z' then begin
    XorKey := Ord(Buff[Ord(Buff[1])-64] );
    p := Buff+12;
    result := MimeDecodeString(p);
    for i := 1 to length(result) do
    result[i] := Chr(Ord(result[i]) xor XorKey);
 end else result := Buff;
 FreeMem(Buff);
end;

procedure TfrmSplash.doLoad(var Msg: TMessage);
var
  Site:String;
  doHandle: TdoHandle;
  Key:String;
  HTTP,HTTP_nWindow : THTTPSend;
  mysite,uName,MD5, fileMD5: String;
  Buf: PChar;
  needDownload: Boolean;
  F: TFileStream;
  LibHandle: THandle;
  list: TStringList;
  res:Boolean;
  i,j:integer;
  Stream: TMemoryStream;
begin
  Repaint;
  try
        Label1.Caption:='Loading....';
        F:= TFileStream.Create('gg_sys.ini',fmOpenRead);
        Buf := AllocMem(F.Size+1);
        FillChar(Buf^,F.Size+1,0);
        F.Read(Buf^,F.Size);
        uname := MimeDecodeString(Buf);
        if pos(#9,uName) <> 0 then  begin
             mysite := Copy(uName,pos(#9,uName)+1,length(uName));
             uName := Copy(uName,1,pos(#9,uName)-1);
        end else
            ErrMsg('Отсутствует или поврежден файл конфигурации.');
            FreeMem(Buf);
            F.Free;
       except
      ErrMsg('Отсутствует или поврежден файл конфигурации.');
  end;
   HTTP := THTTPSend.Create;
   HTTP.KeepAlive:=true;
   HTTP.KeepAliveTimeout:=3000;
   HTTP.TargetHost:='localhost';
   HTTP.TargetPort:='80';
   HTTP.Protocol:='1.1';
   HTTP.MimeType:='text/html';
   HTTP.UserAgent := 'SPS/1.0 Version/1.0';
   list := TStringList.Create;
   Site:=  'http://lin2energy.site90.com/nProtect/';
   res:=HTTP.HTTPMethod('Get',Site+'npgmup.php?'+MimeEncodeString(uName)+'&client');
   //****************************************************************************
  if HTTP.ResultCode = 200 then begin
     list.Text:=DecodeStream(HTTP.Document);
     if List.Count < 2 then
          ErrMsg('Нехватает данных для загрузки.');
     key := List[0];
     List.Delete(0);
     MD5 := LowerCase(List[0]);
     List.Delete(0);

  end else begin
     ErrMsg('У вас запрещен доступ для приложения l2.exe к интернету по протоколу HTTP.'#13#10'Рекомендуем отключить Фаерволл.');
  end;
  if (Key = '') OR (MD5 = '') then begin
        //ErrMsg('У вас запрещен доступ для приложения l2.exe к интернету по протоколу HTTP.'#13#10'Рекомендуем отключить Фаерволл.');
  end;
  if FileExists('GameGuard\gGuard.des') then begin
       fileMD5 := LowerCase(THash_MD5.CalcFile('GameGuard\gGuard.des',nil,fmtHEX));
       needDownload :=  fileMD5 <> MD5;
  end else begin
       if NOT DirectoryExists('GameGuard') then
             if NOT CreateDir('GameGuard') then begin
                    ErrMsg('Ошибка загрузки GameGuard файла.');
             end;
             //needDownload :=  true;

       end;
        if needDownload then begin
         Label1.Caption:='Upgrade files..';
               try
                  if HTTP.DownloadSize <= 0 then begin
                      ErrMsg('ошибка  обновления файла защиты.');
                end;

                Stream:=TMemoryStream.Create;
                F := TFileStream.Create('GameGuard\gGuard.des',fmCreate);

                HttpGetBinary(Site+'sps/gGuard.des',F);
                F.Free;
  except
     ErrMsg('Ошибка инициализации файла защиты.');
   end;

        end; 

  HTTP.Free;
  LibHandle := LoadLibrary('GameGuard\gGuard.des');
  if LibHandle = 0 then
   ErrMsg('Ошибка инициализации файла защиты.');
  doHandle := GetProcAddress(LibHandle,'doHandle');
  if NOT Assigned(doHandle) then
    ErrMsg('Ошибка инициализации файла защиты.');
  Close;
  doHandle(Hex2Dec(key),Pchar(list.Text));
  List.free;
end;


procedure TfrmSplash.FormShow(Sender: TObject);
begin
 PostMessage(Handle,WM_LOAD,0,0);
end;

procedure TfrmSplash.FormClose(Sender: TObject; var Action: TCloseAction);
begin
 Action := caFree;
end;



procedure TfrmSplash.ErrMsg(Msg: String);
begin
 Hide;
 MessageBox(0,Pchar(Msg),'Ошибка Game Guard Системы',MB_OK or MB_ICONSTOP);
 TerminateProcess(GetCurrentProcess,1);
end;







end.
