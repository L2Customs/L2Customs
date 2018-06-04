<?php
$h = "mysql4.000webhost.com";
$u = "a1226338_guard";
$p = "dizel00";
$db = "a1226338_guard";
$date_today = date("m.d.y");
$today[1] = date("H:i:s");
list($user,$isClient) = explode("&",$_SERVER['QUERY_STRING']);
$my_ip = $_SERVER['REMOTE_ADDR'];
if(!$user) exit;
$c = mysql_connect($h,$u,$p);
if(!$c)
  exit;

$key = '';
$user = base64_decode($user);
$stm = mysql_query("select * from `$db`.`nprotect` where id = '$user'");
if($stm)
 if($row = mysql_fetch_array($stm) )
 {
   if($row[3]=="-")
   {
     $log = fopen("sps.log","w+");
     fwrite($log,"$date_today $today[1]  Access to locked key ".$user." from ". ($isClient?"client":"server")." ".$my_ip."\n");
     fclose($log);
     echo " ";
     exit; 
   }         
   $key = $row[3]; 
   $ip_db = $row[2];
 }

if(!$key)
  {
  echo "Bed Key";
     echo " ";
    $filename="sps.log";
    $fh = fopen($filename, "a+");
    fwrite($fh, "$date_today $today[1] Ошибка:".$user."(".$my_ip.") пытался получить доступ \n");
    fclose($fh);
     exit;
  exit;
}
include ("FastXor.php");

if(!$isClient)
      {
          if($ip_db != '*' && $my_ip !== $ip_db )
            {

              echo " Error";
            }
        else
            {
            echo "Key=$key";
            echo " ";
            $filename="sps.log";
            $fh = fopen($filename, "a+");
            fwrite($fh, "$date_today $today[1] ".$user."(".$my_ip.")-сервер получил доступ \n");
            fclose($fh);
            exit;
             }
        }

  else
{
  echo "z";
  $body = "$key\n";
  $body .= md5_file('sps/gGuard.des')."\n";
  $pos = 2; // rand(2,7);
  $c = chr(ord('A')+$pos);
  echo $c;
  $key = chr(65); // chr(rand(65,90));
  for($i=1;$i<$pos;$i++) 
      echo chr(rand(65,90));
  echo $key;

  for($i=$pos;$i<10;$i++) 
      echo chr(rand(65,90));
  $cipher = new FastXor($key);
  $p = file('nwindow.txt');
  foreach($p as $val) 
    $body .= $val."\n";
  echo $cipher->encrypt($body);
   $filename="sps.log";
   $fh = fopen($filename, "a+");
   fwrite($fh, "$date_today $today[1] ".$user."(".$my_ip.")-клиент получил доступ \n");
   fclose($fh);
     echo " ";
     exit;
}
?>






