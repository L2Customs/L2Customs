Imports System.Text.Encoding
Imports System.IO
Imports System.Text.RegularExpressions

Public Class Form1
    Public Function Is_numeric(ByVal key As Keys)
        Select Case key
            Case Keys.D0, Keys.D1, Keys.D2, Keys.D3, Keys.D4, Keys.D5, Keys.D6, Keys.D7, Keys.D8, Keys.D9, 46, Keys.Back
                Return True
        End Select
        Return False
    End Function

    Private Sub TextBox1_KeyPress(ByVal sender As Object, ByVal e As KeyPressEventArgs) Handles TextBox1.KeyPress
        If Not Is_numeric(Asc(e.KeyChar)) Then
            e.Handled = True
        End If
    End Sub

    Private Sub Button1_Click(ByVal sender As System.Object, ByVal e As EventArgs) Handles Button1.Click
        Try
            If Regex.IsMatch(TextBox1.Text, "\b\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}\b") Then
                File.WriteAllBytes(Application.StartupPath & "\DSETUP.dll", My.Resources.DSETUP)
                Dim bw As BinaryWriter = New BinaryWriter(File.Open(Application.StartupPath & "\DSETUP.dll", FileMode.Open, FileAccess.ReadWrite))
                bw.BaseStream.Seek(&H545C, SeekOrigin.Begin)
                bw.Write([Default].GetBytes(TextBox1.Text))
                For i = 0 To (15 - TextBox1.Text.Length) - 1
                    bw.Write(False)
                Next
                bw.Close()
                MsgBox("Patch successful", MsgBoxStyle.Information, "DSETUP")
            Else
                MsgBox("This is not a valid IP!", 16, "Error")
            End If
        Catch ex As Exception
            MsgBox(ex.Message)
        End Try
    End Sub
End Class
