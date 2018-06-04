{--------------------------------------------------------------------------------
Licencing issues:
09-January-2002      ©Cristian Nicola
Note:
 Mysql is copyright by MySQL AB. Refer to their site ( http://www.mysql.com )
for licencing issues.
 Zlib is copyright by Jean-loup Gailly and Mark Adler. Refer to their site for
licencing issues. ( http://www.info-zip.org/pub/infozip/zlib/ )

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.

NOTES:
  1. The origin of this software must not be misrepresented; you must not
     claim that you wrote the original software. If you use this software
     in a product, an acknowledgment in the product documentation would be
     appreciated.
  2. Altered source versions must be plainly marked as such, and must not be
     misrepresented as being the original software.
  3. If you are using it for a commercial software it must be open source and
     it must include full source code of this library in an unaltered fashion
     or you would need to ask for permission to use it. This library will be
     considered donationware which means if you want to contribute with any money
     or hardware you are more than welcome.
  4. This notice may not be removed or altered from any source distribution.

  Cristian Nicola
  n_cristian@hotmail.com

If you use the mysqldirect library in a product, i would appreciate *not*
receiving lengthy legal documents to sign. The sources are provided
for free but without warranty of any kind.  The library has been
entirely written by Cristian Nicola after libmysql of MYSQL AB.
--------------------------------------------------------------------------------}
unit uMysqlHelpers;
////////////////////////////////////////////////////////////////////////////////
// Various functions to make your life easyer
// - all types of refresh
// - client/server flag checking
// - field definition flags checking
// - escape function (for using binary data in queryes)

interface

{$I mysqlinc.inc}

uses
  sysutils, uMysqlClient, uMysqlCT;

//refresh
function REF_GRANT(aMysql:TMysqlClient):boolean;
function REF_LOG(aMysql:TMysqlClient):boolean;
function REF_TABLES(aMysql:TMysqlClient):boolean;
function REF_HOSTS(aMysql:TMysqlClient):boolean;
function REF_STATUS(aMysql:TMysqlClient):boolean;
function REF_THREADS(aMysql:TMysqlClient):boolean;
function REF_SLAVE(aMysql:TMysqlClient):boolean;
function REF_MASTER(aMysql:TMysqlClient):boolean;
function REF_READ_LOCK(aMysql:TMysqlClient):boolean;
function REF_FAST(aMysql:TMysqlClient):boolean;
//client flags
function HasLONGPASSWORD(aFlag:longint):boolean;
function HasFOUNDROWS(aFlag:longint):boolean;
function HasLONGFLAG(aFlag:longint):boolean;
function HasCONNECTWITHDB(aFlag:longint):boolean;
function HasNOSCHEMA(aFlag:longint):boolean;
function HasCOMPRESS(aFlag:longint):boolean;
function HasODBC(aFlag:longint):boolean;
function HasLOCALFILES(aFlag:longint):boolean;
function HasIGNORESPACE(aFlag:longint):boolean;
function HasCHANGEUSER(aFlag:longint):boolean;
function HasINTERACTIVE(aFlag:longint):boolean;
function HasSSL(aFlag:longint):boolean;
function HasIGNORESIGPIPE(aFlag:longint):boolean;
function HasTRANSACTIONS(aFlag:longint):boolean;
function SetLONGPASSWORD(aFlag:longint; aValue:boolean):longint;
function SetFOUNDROWS(aFlag:longint; aValue:boolean):longint;
function SetLONGFLAG(aFlag:longint; aValue:boolean):longint;
function SetCONNECTWITHDB(aFlag:longint; aValue:boolean):longint;
function SetNOSCHEMA(aFlag:longint; aValue:boolean):longint;
function SetCOMPRESS(aFlag:longint; aValue:boolean):longint;
function SetODBC(aFlag:longint; aValue:boolean):longint;
function SetLOCALFILES(aFlag:longint; aValue:boolean):longint;
function SetIGNORESPACE(aFlag:longint; aValue:boolean):longint;
function SetCHANGEUSER(aFlag:longint; aValue:boolean):longint;
function SetINTERACTIVE(aFlag:longint; aValue:boolean):longint;
function SetSSL(aFlag:longint; aValue:boolean):longint;
function SetIGNORESIGPIPE(aFlag:longint; aValue:boolean):longint;
function SetTRANSACTIONS(aFlag:longint; aValue:boolean):longint;
//fields
function IsNOTNULL(aFieldDef:PMysql_FieldDef):boolean;
function IsPRIKEY(aFieldDef:PMysql_FieldDef):boolean;
function IsUNIQUEKEY(aFieldDef:PMysql_FieldDef):boolean;
function IsMULTIPLEKEY(aFieldDef:PMysql_FieldDef):boolean;
function IsBLOB(aFieldDef:PMysql_FieldDef):boolean;
function IsUNSIGNED(aFieldDef:PMysql_FieldDef):boolean;
function IsZEROFILL(aFieldDef:PMysql_FieldDef):boolean;
function IsBINARY(aFieldDef:PMysql_FieldDef):boolean;
function IsENUM(aFieldDef:PMysql_FieldDef):boolean;
function IsAUTOINCREMENT(aFieldDef:PMysql_FieldDef):boolean;
function IsTIMESTAMP(aFieldDef:PMysql_FieldDef):boolean;
function IsSET(aFieldDef:PMysql_FieldDef):boolean;
function IsNUM(aFieldDef:PMysql_FieldDef):boolean;
function IsPARTKEY(aFieldDef:PMysql_FieldDef):boolean;
function IsGROUP(aFieldDef:PMysql_FieldDef):boolean;
function IsUNIQUE(aFieldDef:PMysql_FieldDef):boolean;
//binary fields (blobs) functions)
function EscapeStr(aString: string): string;

implementation
////////////////////////////////////////////////////////////////////////////////
//different type of refresh if one does not want to read the docs

//attempt to execute a refresh grant
function REF_GRANT(aMysql:TMysqlClient):boolean;
begin
  result:=false;
  if Assigned(aMysql) then
    result:=aMysql.refresh(REFRESH_GRANT);
end;

//attempt to execute a refresh logs
function REF_LOG(aMysql:TMysqlClient):boolean;
begin
  result:=false;
  if Assigned(aMysql) then
    result:=aMysql.refresh(REFRESH_LOG);
end;

//attempt to execute a refresh tables
function REF_TABLES(aMysql:TMysqlClient):boolean;
begin
  result:=false;
  if Assigned(aMysql) then
    result:=aMysql.refresh(REFRESH_TABLES);
end;

//attempt to execute a refresh hosts
function REF_HOSTS(aMysql:TMysqlClient):boolean;
begin
  result:=false;
  if Assigned(aMysql) then
    result:=aMysql.refresh(REFRESH_HOSTS);
end;

//attempt to execute a refresh status
function REF_STATUS(aMysql:TMysqlClient):boolean;
begin
  result:=false;
  if Assigned(aMysql) then
    result:=aMysql.refresh(REFRESH_STATUS);
end;

//attempt to execute a refresh threads
function REF_THREADS(aMysql:TMysqlClient):boolean;
begin
  result:=false;
  if Assigned(aMysql) then
    result:=aMysql.refresh(REFRESH_THREADS);
end;

//attempt to execute a refresh slave
function REF_SLAVE(aMysql:TMysqlClient):boolean;
begin
  result:=false;
  if Assigned(aMysql) then
    result:=aMysql.refresh(REFRESH_SLAVE);
end;

//attempt to execute a refresh master
function REF_MASTER(aMysql:TMysqlClient):boolean;
begin
  result:=false;
  if Assigned(aMysql) then
    result:=aMysql.refresh(REFRESH_MASTER);
end;

//attempt to execute a refresh read lock
function REF_READ_LOCK(aMysql:TMysqlClient):boolean;
begin
  result:=false;
  if Assigned(aMysql) then
    result:=aMysql.refresh(REFRESH_READ_LOCK);
end;

//attempt to execute a refresh fast
function REF_FAST(aMysql:TMysqlClient):boolean;
begin
  result:=false;
  if Assigned(aMysql) then
    result:=aMysql.refresh(REFRESH_FAST);
end;

////////////////////////////////////////////////////////////////////////////////
//different type of flags check for client/server flags

//returns true if a client flag has long password
function HasLONGPASSWORD(aFlag:longint):boolean;
begin
  result:=aFlag and CLIENT_LONG_PASSWORD = CLIENT_LONG_PASSWORD;
end;

//returns true if a client flag has found rows
function HasFOUNDROWS(aFlag:longint):boolean;
begin
  result:=aFlag and CLIENT_FOUND_ROWS = CLIENT_FOUND_ROWS;
end;

//returns true if a client flag has long flag
function HasLONGFLAG(aFlag:longint):boolean;
begin
  result:=aFlag and CLIENT_LONG_FLAG = CLIENT_LONG_FLAG;
end;

//returns true if a client flag has connect with db
function HasCONNECTWITHDB(aFlag:longint):boolean;
begin
  result:=aFlag and CLIENT_CONNECT_WITH_DB = CLIENT_CONNECT_WITH_DB;
end;

//returns true if a client flag has no schema
function HasNOSCHEMA(aFlag:longint):boolean;
begin
  result:=aFlag and CLIENT_NO_SCHEMA = CLIENT_NO_SCHEMA;
end;

//returns true if a client flag has compress
function HasCOMPRESS(aFlag:longint):boolean;
begin
  result:=aFlag and CLIENT_COMPRESS = CLIENT_COMPRESS;
end;

//returns true if a client flag has ODBC
function HasODBC(aFlag:longint):boolean;
begin
  result:=aFlag and CLIENT_ODBC = CLIENT_ODBC;
end;

//returns true if a client flag has local files
function HasLOCALFILES(aFlag:longint):boolean;
begin
  result:=aFlag and CLIENT_LOCAL_FILES = CLIENT_LOCAL_FILES;
end;

//returns true if a client flag has ignore spaces
function HasIGNORESPACE(aFlag:longint):boolean;
begin
  result:=aFlag and CLIENT_IGNORE_SPACE = CLIENT_IGNORE_SPACE;
end;

//returns true if a client flag has change user
function HasCHANGEUSER(aFlag:longint):boolean;
begin
  result:=aFlag and CLIENT_CHANGE_USER = CLIENT_CHANGE_USER;
end;

//returns true if a client flag has interactive
function HasINTERACTIVE(aFlag:longint):boolean;
begin
  result:=aFlag and CLIENT_INTERACTIVE = CLIENT_INTERACTIVE;
end;

//returns true if a client flag has ssl
function HasSSL(aFlag:longint):boolean;
begin
  result:=aFlag and CLIENT_SSL = CLIENT_SSL;
end;

//returns true if a client flag has ignore sigpipe
function HasIGNORESIGPIPE(aFlag:longint):boolean;
begin
  result:=aFlag and CLIENT_IGNORE_SIGPIPE = CLIENT_IGNORE_SIGPIPE;
end;

//returns true if a client flag has transcations
function HasTRANSACTIONS(aFlag:longint):boolean;
begin
  result:=aFlag and CLIENT_TRANSACTIONS= CLIENT_TRANSACTIONS;
end;

//takes aflags as imputs and sets/resets into the result : long password
function SetLONGPASSWORD(aFlag:longint; aValue:boolean):longint;
begin
  if aValue then
    result:=aFlag or CLIENT_LONG_PASSWORD
  else
    result:=aFlag and not CLIENT_LONG_PASSWORD;
end;

//takes aflags as imputs and sets/resets into the result : found rows
function SetFOUNDROWS(aFlag:longint; aValue:boolean):longint;
begin
  if aValue then
    result:=aFlag or CLIENT_FOUND_ROWS
  else
    result:=aFlag and not CLIENT_FOUND_ROWS;
end;

//takes aflags as imputs and sets/resets into the result : long flag
function SetLONGFLAG(aFlag:longint; aValue:boolean):longint;
begin
  if aValue then
    result:=aFlag or CLIENT_LONG_FLAG
  else
    result:=aFlag and not CLIENT_LONG_FLAG;
end;

//takes aflags as imputs and sets/resets into the result : connect with db
function SetCONNECTWITHDB(aFlag:longint; aValue:boolean):longint;
begin
  if aValue then
    result:=aFlag or CLIENT_CONNECT_WITH_DB
  else
    result:=aFlag and not CLIENT_CONNECT_WITH_DB;
end;

//takes aflags as imputs and sets/resets into the result : no schema
function SetNOSCHEMA(aFlag:longint; aValue:boolean):longint;
begin
  if aValue then
    result:=aFlag or CLIENT_NO_SCHEMA
  else
    result:=aFlag and not CLIENT_NO_SCHEMA;
end;

//takes aflags as imputs and sets/resets into the result : compress
function SetCOMPRESS(aFlag:longint; aValue:boolean):longint;
begin
  if aValue then
    result:=aFlag or CLIENT_COMPRESS
  else
    result:=aFlag and not CLIENT_COMPRESS;
end;

//takes aflags as imputs and sets/resets into the result : ODBC
function SetODBC(aFlag:longint; aValue:boolean):longint;
begin
  if aValue then
    result:=aFlag or CLIENT_ODBC
  else
    result:=aFlag and not CLIENT_ODBC;
end;

//takes aflags as imputs and sets/resets into the result : local files
function SetLOCALFILES(aFlag:longint; aValue:boolean):longint;
begin
  if aValue then
    result:=aFlag or CLIENT_LOCAL_FILES
  else
    result:=aFlag and not CLIENT_LOCAL_FILES;
end;

//takes aflags as imputs and sets/resets into the result : ignore spaces
function SetIGNORESPACE(aFlag:longint; aValue:boolean):longint;
begin
  if aValue then
    result:=aFlag or CLIENT_IGNORE_SPACE
  else
    result:=aFlag and not CLIENT_IGNORE_SPACE;
end;

//takes aflags as imputs and sets/resets into the result : change user
function SetCHANGEUSER(aFlag:longint; aValue:boolean):longint;
begin
  if aValue then
    result:=aFlag or CLIENT_CHANGE_USER
  else
    result:=aFlag and not CLIENT_CHANGE_USER;
end;

//takes aflags as imputs and sets/resets into the result : interactive
function SetINTERACTIVE(aFlag:longint; aValue:boolean):longint;
begin
  if aValue then
    result:=aFlag or CLIENT_INTERACTIVE
  else
    result:=aFlag and not CLIENT_INTERACTIVE;
end;

//takes aflags as imputs and sets/resets into the result : ssl
function SetSSL(aFlag:longint; aValue:boolean):longint;
begin
  if aValue then
    result:=aFlag or CLIENT_SSL
  else
    result:=aFlag and not CLIENT_SSL;
end;

//takes aflags as imputs and sets/resets into the result : ignore sigpipe
function SetIGNORESIGPIPE(aFlag:longint; aValue:boolean):longint;
begin
  if aValue then
    result:=aFlag or CLIENT_IGNORE_SIGPIPE
  else
    result:=aFlag and not CLIENT_IGNORE_SIGPIPE;
end;

//takes aflags as imputs and sets/resets into the result : transactions
function SetTRANSACTIONS(aFlag:longint; aValue:boolean):longint;
begin
  if aValue then
    result:=aFlag or CLIENT_TRANSACTIONS
  else
    result:=aFlag and not CLIENT_TRANSACTIONS;
end;

////////////////////////////////////////////////////////////////////////////////
//different type of flags check for fields

//returns true if not null
function IsNOTNULL(aFieldDef:PMysql_FieldDef):boolean;
begin
  result:=false;
  if assigned(aFieldDef) then
    result:=aFieldDef.Flags and NOT_NULL_FLAG = NOT_NULL_FLAG;
end;

//returns true if primary key
function IsPRIKEY(aFieldDef:PMysql_FieldDef):boolean;
begin
  result:=false;
  if assigned(aFieldDef) then
    result:=aFieldDef.Flags and PRI_KEY_FLAG = PRI_KEY_FLAG;
end;

//returns true if unique key
function IsUNIQUEKEY(aFieldDef:PMysql_FieldDef):boolean;
begin
  result:=false;
  if assigned(aFieldDef) then
    result:=aFieldDef.Flags and UNIQUE_KEY_FLAG = UNIQUE_KEY_FLAG;
end;

//returns true if multiple key
function IsMULTIPLEKEY(aFieldDef:PMysql_FieldDef):boolean;
begin
  result:=false;
  if assigned(aFieldDef) then
    result:=aFieldDef.Flags and MULTIPLE_KEY_FLAG = MULTIPLE_KEY_FLAG;
end;

//returns true if blob
function IsBLOB(aFieldDef:PMysql_FieldDef):boolean;
begin
  result:=false;
  if assigned(aFieldDef) then
    result:=aFieldDef.Flags and BLOB_FLAG = BLOB_FLAG;
end;

//returns true if unsigned
function IsUNSIGNED(aFieldDef:PMysql_FieldDef):boolean;
begin
  result:=false;
  if assigned(aFieldDef) then
    result:=aFieldDef.Flags and UNSIGNED_FLAG = UNSIGNED_FLAG;
end;

//returns true if zero fill
function IsZEROFILL(aFieldDef:PMysql_FieldDef):boolean;
begin
  result:=false;
  if assigned(aFieldDef) then
    result:=aFieldDef.Flags and ZEROFILL_FLAG = ZEROFILL_FLAG;
end;

//returns true if binary
function IsBINARY(aFieldDef:PMysql_FieldDef):boolean;
begin
  result:=false;
  if assigned(aFieldDef) then
    result:=aFieldDef.Flags and BINARY_FLAG = BINARY_FLAG;
end;

//returns true if enum
function IsENUM(aFieldDef:PMysql_FieldDef):boolean;
begin
  result:=false;
  if assigned(aFieldDef) then
    result:=aFieldDef.Flags and ENUM_FLAG = ENUM_FLAG;
end;

//returns true if autoincrement
function IsAUTOINCREMENT(aFieldDef:PMysql_FieldDef):boolean;
begin
  result:=false;
  if assigned(aFieldDef) then
    result:=aFieldDef.Flags and AUTO_INCREMENT_FLAG = AUTO_INCREMENT_FLAG;
end;

//returns true if timestamp
function IsTIMESTAMP(aFieldDef:PMysql_FieldDef):boolean;
begin
  result:=false;
  if assigned(aFieldDef) then
    result:=aFieldDef.Flags and TIMESTAMP_FLAG = TIMESTAMP_FLAG;
end;

//returns true if set
function IsSET(aFieldDef:PMysql_FieldDef):boolean;
begin
  result:=false;
  if assigned(aFieldDef) then
    result:=aFieldDef.Flags and SET_FLAG = SET_FLAG;
end;

//returns true if numeric
function IsNUM(aFieldDef:PMysql_FieldDef):boolean;
begin
  result:=false;
  if assigned(aFieldDef) then
    result:=aFieldDef.Flags and NUM_FLAG = NUM_FLAG;
end;

//returns true if part of a key
function IsPARTKEY(aFieldDef:PMysql_FieldDef):boolean;
begin
  result:=false;
  if assigned(aFieldDef) then
    result:=aFieldDef.Flags and PART_KEY_FLAG = PART_KEY_FLAG;
end;

//returns true if group
function IsGROUP(aFieldDef:PMysql_FieldDef):boolean;
begin
  result:=false;
  if assigned(aFieldDef) then
    result:=aFieldDef.Flags and GROUP_FLAG = GROUP_FLAG;
end;

//returns true unique
function IsUNIQUE(aFieldDef:PMysql_FieldDef):boolean;
begin
  result:=false;
  if assigned(aFieldDef) then
    result:=aFieldDef.Flags and UNIQUE_FLAG = UNIQUE_FLAG;
end;


////////////////////////////////////////////////////////////////////////////////
// convert a string to escape string format as in c syntax
// when sending a query with blobs a blob should be escaped and enclosed between '' or ""
function EscapeStr(aString: string): string;
var
  I, j, k: Integer;
  pc: PChar;
begin
  j := 0;
  k := Length(aString);
  //how many chars needs escape mark?
  for I := 1 to k do
    if aString[I] in ['''','"','\',#9,#10,#13,#0] then
      Inc(j);
  SetLength(Result, k + j); //dimension the result to right size
  pc := PChar(Result); //a pchar for walking the result
  for I := 1 to k do
    begin
      if aString[I] in ['''','"','\',#9,#10,#13,#0] then
        begin //if this char needs escape
          pc^ := '\'; //escape
          Inc(pc);//next char
          case aString[I] of
            #9:  pc^ := 't';
            #10: pc^ := 'n';
            #13: pc^ := 'r';
            #0:  pc^ := '0';
          else
            pc^ := aString[I];
          end;
        end
      else
        pc^ := aString[I];
      Inc(pc);//next char
    end;
end;

end.
