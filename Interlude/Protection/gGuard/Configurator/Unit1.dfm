object frmMain: TfrmMain
  Left = 315
  Top = 228
  BorderStyle = bsDialog
  Caption = 'GuardConfigurator'
  ClientHeight = 71
  ClientWidth = 422
  Color = clBtnFace
  Font.Charset = DEFAULT_CHARSET
  Font.Color = clWindowText
  Font.Height = -11
  Font.Name = 'MS Sans Serif'
  Font.Style = []
  OldCreateOrder = False
  PixelsPerInch = 96
  TextHeight = 13
  object Label1: TLabel
    Left = 12
    Top = 15
    Width = 113
    Height = 16
    Caption = #1048#1084#1103' '#1079#1072#1082#1072#1079#1095#1080#1082#1072
    Font.Charset = DEFAULT_CHARSET
    Font.Color = clWindowText
    Font.Height = -13
    Font.Name = 'MS Sans Serif'
    Font.Style = [fsBold]
    ParentFont = False
  end
  object warn: TLabel
    Left = 7
    Top = 41
    Width = 141
    Height = 24
    AutoSize = False
    Font.Charset = DEFAULT_CHARSET
    Font.Color = clRed
    Font.Height = -13
    Font.Name = 'MS Sans Serif'
    Font.Style = [fsBold]
    ParentFont = False
  end
  object regName: TEdit
    Left = 134
    Top = 11
    Width = 276
    Height = 21
    TabOrder = 0
  end
  object btnSave: TButton
    Left = 336
    Top = 34
    Width = 75
    Height = 25
    Caption = #1057#1086#1093#1088#1072#1085#1080#1090#1100
    TabOrder = 1
    OnClick = btnSaveClick
  end
  object SaveDialog1: TSaveDialog
    Filter = 'ini|*.ini'
    Left = 10
    Top = 13
  end
end
