{--------------------------------------------------------------------------------
Licencing issues:
11-February-2004      ©Cristian Nicola
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
unit uMysqlSSL;
////////////////////////////////////////////////////////////////////////////////
// Varios imports from open ssl dll's
// at a later stage this may import the obj files (for no external dlls)
// currently uses libeay32.dll and ssleay32.dll

interface

{$I mysqlinc.inc}

uses
  sysutils{, Dialogs}; //for debug

{$IFDEF HAVE_SSL}

type
  st_VioSSLConnectorFd = record
    ssl_context_:pointer;
    ssl_method_:pointer;
  end;
{$IFDEF _WIN_}
function SSL_write(s:pointer; var buff;len:longint):longint;cdecl;external 'ssleay32.dll';
function SSL_read(s: pointer; var Buf; len: longint): longint; cdecl;external 'ssleay32.dll';
function SSL_get_error(s:pointer;ret_code:longint):longint;cdecl;external 'ssleay32.dll';
function SSL_shutdown(s:pointer):longint;cdecl;external 'ssleay32.dll';
function SSL_state(s:pointer):longint;cdecl;external 'ssleay32.dll';
procedure SSL_free(s:pointer);cdecl;external 'ssleay32.dll';
procedure SSL_load_error_strings;cdecl;external 'ssleay32.dll';
function TLSv1_client_method:pointer;cdecl;external 'ssleay32.dll';
function SSL_CTX_new(meth:pointer):pointer;cdecl;external 'ssleay32.dll';
function SSL_CTX_set_cipher_list(actx:pointer;const str:pchar):longint;cdecl;external 'ssleay32.dll';
function SSL_new(s:pointer):pointer;cdecl;external 'ssleay32.dll';
function SSL_clear(s:pointer):longint;cdecl;external 'ssleay32.dll';
function SSL_SESSION_set_timeout(s:pointer; t:cardinal):longint;cdecl;external 'ssleay32.dll';
function SSL_get_session(s:pointer):pointer;cdecl;external 'ssleay32.dll';
function SSL_set_fd(s:pointer; fd:longint):longint;cdecl;external 'ssleay32.dll';
procedure SSL_set_connect_state(s:pointer);cdecl;external 'ssleay32.dll';
function SSL_do_handshake(s:pointer):longint;cdecl;external 'ssleay32.dll';
function SSL_get_peer_certificate(s:pointer):pointer;cdecl;external 'ssleay32.dll';
function SSL_set_session(_to:pointer;session:pointer):longint;cdecl;external 'ssleay32.dll';
function SSL_connect(s:pointer):longint;cdecl;external 'ssleay32.dll';
function SSL_CIPHER_get_name(c:pointer):pchar;cdecl;external 'ssleay32.dll';
function SSL_get_current_cipher(s:pointer):pointer;cdecl;external 'ssleay32.dll';
procedure SSL_CTX_set_verify(actx:pointer;mode:longint;acallback:pointer);cdecl;external 'ssleay32.dll';
function SSL_CTX_load_verify_locations(actx:pointer; const CAfile:pchar;const CApath:pchar):longint;cdecl;external 'ssleay32.dll';
function SSL_CTX_set_default_verify_paths(actx:pointer):longint;cdecl;external 'ssleay32.dll';
function SSL_CTX_use_certificate_file(actx:pointer; const afile:pchar; atype:longint):longint;cdecl;external 'ssleay32.dll';
function SSL_CTX_use_PrivateKey_file(actx:pointer; const afile:pchar; atype:longint):longint;cdecl;external 'ssleay32.dll';
function SSL_CTX_check_private_key(actx:pointer):longint;cdecl;external 'ssleay32.dll';
function DH_new:pointer;cdecl;external 'libeay32.dll';
function DH_free(dh:pointer):longint;cdecl;external 'libeay32.dll';
procedure OpenSSL_add_all_algorithms;cdecl;external 'libeay32.dll';
function BN_bin2bn(const s:pointer;len:longint;ret:pointer):pointer;cdecl;external 'libeay32.dll';
function X509_get_subject_name(a:pointer):pointer;cdecl;external 'libeay32.dll';
function X509_NAME_oneline(a:pointer;buf:pchar;size:longint):pchar;cdecl;external 'libeay32.dll';
function X509_STORE_CTX_get_error_depth(actx:pointer):longint;cdecl;external 'libeay32.dll';
function X509_STORE_CTX_get_error(actx:pointer):longint;cdecl;external 'libeay32.dll';
function X509_STORE_CTX_get_current_cert(actx:pointer):pointer;cdecl;external 'libeay32.dll';
function SSL_CTX_ctrl(actx:pointer;a1:longint;a2:longint; adh:pointer):longint;cdecl;external 'ssleay32.dll';
function X509_verify_cert_error_string(n:longint):pchar;cdecl;external 'libeay32.dll';
function X509_get_issuer_name(a:pointer):pointer;cdecl;external 'libeay32.dll';
function ERR_get_error_line_data(const afile:pointer;line:pointer;const data:pointer;flags:pointer):longint;cdecl;external 'libeay32.dll';
function ERR_error_string(e:cardinal;buf:pchar):pchar;cdecl;external 'libeay32.dll';
procedure X509_free(a:pointer);cdecl;external 'libeay32.dll';
{$ELSE}
function SSL_write(s:pointer; var buff;len:longint):longint;cdecl;external 'libssl.so.0';
function SSL_read(s: pointer; var Buf; len: longint): longint; cdecl;external 'libssl.so.0';
function SSL_get_error(s:pointer;ret_code:longint):longint;cdecl;external 'libssl.so.0';
function SSL_shutdown(s:pointer):longint;cdecl;external 'libssl.so.0';
function SSL_state(s:pointer):longint;cdecl;external 'libssl.so.0';
procedure SSL_free(s:pointer);cdecl;external 'libssl.so.0';
procedure SSL_load_error_strings;cdecl;external 'libssl.so.0';
function TLSv1_client_method:pointer;cdecl;external 'libssl.so.0';
function SSL_CTX_new(meth:pointer):pointer;cdecl;external 'libssl.so.0';
function SSL_CTX_set_cipher_list(actx:pointer;const str:pchar):longint;cdecl;external 'libssl.so.0';
function SSL_new(s:pointer):pointer;cdecl;external 'libssl.so.0';
function SSL_clear(s:pointer):longint;cdecl;external 'libssl.so.0';
function SSL_SESSION_set_timeout(s:pointer; t:cardinal):longint;cdecl;external 'libssl.so.0';
function SSL_get_session(s:pointer):pointer;cdecl;external 'libssl.so.0';
function SSL_set_fd(s:pointer; fd:longint):longint;cdecl;external 'libssl.so.0';
procedure SSL_set_connect_state(s:pointer);cdecl;external 'libssl.so.0';
function SSL_do_handshake(s:pointer):longint;cdecl;external 'libssl.so.0';
function SSL_get_peer_certificate(s:pointer):pointer;cdecl;external 'libssl.so.0';
function SSL_set_session(_to:pointer;session:pointer):longint;cdecl;external 'libssl.so.0';
function SSL_connect(s:pointer):longint;cdecl;external 'libssl.so.0';
function SSL_CIPHER_get_name(c:pointer):pchar;cdecl;external 'libssl.so.0';
function SSL_get_current_cipher(s:pointer):pointer;cdecl;external 'libssl.so.0';
procedure SSL_CTX_set_verify(actx:pointer;mode:longint;acallback:pointer);cdecl;external 'libssl.so.0';
function SSL_CTX_load_verify_locations(actx:pointer; const CAfile:pchar;const CApath:pchar):longint;cdecl;external 'libssl.so.0';
function SSL_CTX_set_default_verify_paths(actx:pointer):longint;cdecl;external 'libssl.so.0';
function SSL_CTX_use_certificate_file(actx:pointer; const afile:pchar; atype:longint):longint;cdecl;external 'libssl.so.0';
function SSL_CTX_use_PrivateKey_file(actx:pointer; const afile:pchar; atype:longint):longint;cdecl;external 'libssl.so.0';
function SSL_CTX_check_private_key(actx:pointer):longint;cdecl;external 'libssl.so.0';
function DH_new:pointer;cdecl;external 'libssl.so.0';
function DH_free(dh:pointer):longint;cdecl;external 'libssl.so.0';
procedure OpenSSL_add_all_algorithms;cdecl;external 'libssl.so.0';
function BN_bin2bn(const s:pointer;len:longint;ret:pointer):pointer;cdecl;external 'libssl.so.0';
function X509_get_subject_name(a:pointer):pointer;cdecl;external 'libssl.so.0';
function X509_NAME_oneline(a:pointer;buf:pchar;size:longint):pchar;cdecl;external 'libssl.so.0';
function X509_STORE_CTX_get_error_depth(actx:pointer):longint;cdecl;external 'libssl.so.0';
function X509_STORE_CTX_get_error(actx:pointer):longint;cdecl;external 'libssl.so.0';
function X509_STORE_CTX_get_current_cert(actx:pointer):pointer;cdecl;external 'libssl.so.0';
function SSL_CTX_ctrl(actx:pointer;a1:longint;a2:longint; adh:pointer):longint;cdecl;external 'libssl.so.0';
function X509_verify_cert_error_string(n:longint):pchar;cdecl;external 'libssl.so.0';
function X509_get_issuer_name(a:pointer):pointer;cdecl;external 'libssl.so.0';
function ERR_get_error_line_data(const afile:pointer;line:pointer;const data:pointer;flags:pointer):longint;cdecl;external 'libssl.so.0';
function ERR_error_string(e:cardinal;buf:pchar):pchar;cdecl;external 'libssl.so.0';
procedure X509_free(a:pointer);cdecl;external 'libssl.so.0';
{$ENDIF}

var
  ssl_algorithms_added:boolean = false;
  ssl_error_strings_loaded:boolean = false;
//these are the functions implemented by us
function vio_verify_callback(ok:longint; ctx:pointer):longint;cdecl;
function vio_set_cert_stuff(ctx:pointer; const cert_file:pchar; key_file:pchar):longint;
function get_dh512:pointer;
{$ENDIF}

implementation
{$IFDEF HAVE_SSL}

////////////////////////////////////////////////////////////////////////////////
// the callback for verify on ssl .. we can get errors and details here
// you should remove the showmessages from here
function vio_verify_callback(ok:longint; ctx:pointer):longint;cdecl;
type
  TDummyCTX = record
    pad:array[0..75]of byte;
    error:longint;
    current_cert:pointer;
  end;
var
  buf:array[0..255]of char;
  err_cert:pointer;
  depth, err:longint;
begin
  //get the details
  err_cert:=X509_STORE_CTX_get_current_cert(ctx);
  err:= X509_STORE_CTX_get_error(ctx);
  depth:=X509_STORE_CTX_get_error_depth(ctx);
  X509_NAME_oneline(X509_get_subject_name(err_cert),pchar(@buf),256);
  ok:=1;
  //some more details about the error
  case TDummyCTX(ctx^).error of
    2: //X509_V_ERR_UNABLE_TO_GET_ISSUER_CERT:
        begin
          X509_NAME_oneline(X509_get_issuer_name(TDummyCTX(ctx^).current_cert),buf,256);
          //showmessage('issuer= '+buf);
        end;
    9,13: //X509_V_ERR_CERT_NOT_YET_VALID:
          //X509_V_ERR_ERROR_IN_CERT_NOT_BEFORE_FIELD:
          //DBUG_PRINT("error", ("notBefore"));
          //*ASN1_TIME_print_fp(stderr,X509_get_notBefore(ctx->current_cert));*/
           ;
    10,14: //X509_V_ERR_CERT_HAS_EXPIRED:
            //X509_V_ERR_ERROR_IN_CERT_NOT_AFTER_FIELD:
            //DBUG_PRINT("error", ("notAfter error"));
            //*ASN1_TIME_print_fp(stderr,X509_get_notAfter(ctx->current_cert));*/
            ;
  end;
  result:=ok;
end;

////////////////////////////////////////////////////////////////////////////////
// sets the cert stuff
function vio_set_cert_stuff(ctx:pointer; const cert_file:pchar; key_file:pchar):longint;
begin
  result:=0;
  if (cert_file <> nil) and (cert_file<>'') then //is there anything to set?
    begin
      //use it
      if (SSL_CTX_use_certificate_file(ctx,cert_file,1{SSL_FILETYPE_PEM}) <= 0) then
        exit;
      if (key_file =nil)or(key_file='') then //do we have any key
        key_file := cert_file;
      //use it
      if (SSL_CTX_use_PrivateKey_file(ctx,key_file, 1{SSL_FILETYPE_PEM}) <= 0) then
        exit;
      //let's check it
      if (SSL_CTX_check_private_key(pointer(ctx^))<>0) then
        exit;
    end;
  result:=1;//no errors
end;

////////////////////////////////////////////////////////////////////////////////
// gets a new dh
function get_dh512:pointer;
const
  dh512_g:array[1..1] of byte = ($02);
  dh512_p: array[1..64] of byte =(
    $DA,$58,$3C,$16,$D9,$85,$22,$89,$D0,$E4,$AF,$75,
    $6F,$4C,$CA,$92,$DD,$4B,$E5,$33,$B8,$04,$FB,$0F,
    $ED,$94,$EF,$9C,$8A,$44,$03,$ED,$57,$46,$50,$D3,
    $69,$99,$DB,$29,$D7,$76,$27,$6B,$A2,$D3,$D4,$12,
    $E2,$18,$F4,$DD,$1E,$08,$4C,$F6,$D8,$00,$3E,$7C,
    $47,$74,$E8,$33);

type
  TDHdummy = record
    pad:longint;
    version:longint;
    p:pointer;
    g:pointer;
    //the others are skipped
  end;
var
  dh:pointer;
begin
  result:=nil;
  dh:=DH_new; //grab a dh
  if (dh= nil) then
    exit;
  TDHdummy(dh^).p:=BN_bin2bn(@dh512_p,sizeof(dh512_p),nil); //set p
  TDHdummy(dh^).g:=BN_bin2bn(@dh512_g,sizeof(dh512_g),nil); //set g
  if ((TDHdummy(dh^).p = nil) or (TDHdummy(dh^).g = nil)) then //any errors?
    begin
      dh_free(dh);
      exit;
    end;
  result:=dh;
end;

{$ENDIF}

end.
