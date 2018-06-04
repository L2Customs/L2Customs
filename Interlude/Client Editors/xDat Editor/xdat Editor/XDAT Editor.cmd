@Echo Off
Color F9
Title Loading...

:: Собираем данные о версиях ПО
Set java.external.version=Undefined
For /F "tokens=4-5 delims=. " %%W In ('Ver') Do Set windows.version=%%W.%%X
For /F "tokens=4 delims=()" %%J In ('Java -version 2^>^&1^|Find "SE Runtime Environment"') Do Set java.external.version=%%J

:: Добавляем джаву и либы в переменные окружения
Set Path=%~dp0java\bin;%~dp0java\lib;%~dp0libs;%~dp0bin;%Path%

:: Собираем данные о версиях встроенного ПО
For /F "tokens=4 delims=()" %%J In ('Java -version 2^>^&1^|Find "SE Runtime Environment"') Do Set java.internal.version=%%J
For /F "tokens=2 delims=: " %%X In ('Unzip -p editor.jar META-INF/MANIFEST.MF^|FindStr /BC:"Version"') Do Set xdat.version=%%X

:: Выводим на экран приветствие
:View
CLS
Title XDAT Editor v%xdat.version%
Echo.
Echo.	OS version: 		%windows.version%
Echo.	Editor version: 	%xdat.version%
Echo.	Java version: 		system - %java.external.version%
Echo.				built-in - %java.internal.version%
Echo.

:: Спрашиваем у юзера, чего он хочет запустить
Echo. & Echo Press 'u' for update (internet connection required) & Echo Press 'e' or Enter to launch XDAT editor
Set /P "$Command= Your choice: "
Set "Class="
For %%S In ("XdatEditor:","XdatEditor:e","Updater:u") Do (
	For /F "tokens=1-2 delims=:" %%A In ("%%~S") Do (
		If /I [%$Command%]==[%%B] Set Class=%%A
	)
)

:: Обрабатываем выбор пользователя
If Defined Class (
	Start java\bin\javaw -cp "editor.jar;updater.jar;./libs/*" acmi.l2.clientmod.xdat.%Class%
) Else GoTo :View