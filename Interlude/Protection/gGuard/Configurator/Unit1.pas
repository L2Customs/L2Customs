unit Unit1;

interface

uses
  Windows, Messages, SysUtils, Variants, Classes, Graphics, Controls, Forms,
  Dialogs, StdCtrls;

type
  TfrmMain = class(TForm)
    Label1: TLabel;
    regName: TEdit;
    btnSave: TButton;
    warn: TLabel;
    SaveDialog1: TSaveDialog;
    procedure btnScanClick(Sender: TObject);
    procedure btnSaveClick(Sender: TObject);
  private
    procedure webNamePropertiesButtonClick;
  public
    function webCheck: Boolean;
  end;

var
  frmMain: TfrmMain;

implementation
Uses httpsend,JclMime;
{$R *.dfm}
procedure TfrmMain.webNamePropertiesButtonClick;
begin
 if MessageBox(Handle,'Проверить ссылку на валидность?','Конфигурация',MB_YESNO or MB_ICONQUESTION) = ID_YES then  begin
   webCheck;
 end;
end;


function TfrmMain.webCheck: Boolean;
var HTTP: THTTPSend;
begin
  if Trim(webName.Text) <> '' then begin
    HTTP := THTTPSend.Create;
    HTTP.HTTPMethod('Get',webName.Text);
    try
    try

     if HTTP.ResultCode <> 200 then begin
       warn.Caption := 'Ошибка доступа к серверу '+HTTP.ResultString;
       result := false;
       exit;
     end;
     result := true;
    except
     on E: Exception do begin
       warn.Caption := 'Соединение не установлено';
       result := false;
     end;
    end;
    finally
     HTTP.Free;
    end;
  end else result := false;
end;

procedure TfrmMain.btnScanClick(Sender: TObject);
begin
frmMain.webNamePropertiesButtonClick;
end;

procedure TfrmMain.btnSaveClick(Sender: TObject);

var F: TFileStream;
    Buffer: String;
begin
   if Trim(regName.Text) = '' then begin
          warn.Caption := 'Укажите имя заказчика';
          regName.SetFocus;
          exit;
   end;
   if (webName.Text <> '') AND  (NOT webCheck) then begin
          warn.Caption := 'Укажите сайт или оставьте пустым';
          webName.SetFocus;
          exit;
   end;
   
   SaveDialog1.FileName := 'gg_sys.ini';
   if SaveDialog1.Execute then begin
       Buffer := regName.Text+#9+webName.Text;
          try
              F := TFileStream.Create(SaveDialog1.FileName,fmCreate);
              Buffer := MimeEncodeString(Buffer);
              F.Write(Pchar(Buffer)^,Length(Buffer));
              F.Free;
          except
              MessageBox(Self.Handle,'Ощибка сохранения файла','Неправельно введены данные',MB_OK or MB_ICONSTOP);
          end;
      end;
    end;


end.
