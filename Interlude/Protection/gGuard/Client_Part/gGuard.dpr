library gGuard;

uses
  SysUtils,
  Classes,
  ufrmSplash in 'ufrmSplash.pas' {frmSplash};


{$R *.RES}
procedure DllEntryPoint; stdcall;
begin
end;

exports
  DllEntryPoint;
begin
 frmSplash := TfrmSplash.Create(nil);
 frmSplash.ShowModal;
end.
