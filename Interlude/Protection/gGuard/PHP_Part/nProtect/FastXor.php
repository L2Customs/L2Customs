<?php
class FastXor {
  var $key;
  function FastXor($key) {
    $this->key = $key;
  }
 function hexstr($s) {
  $r = "";
  for($i=0;$i<strlen($s);$i++)
   $r.=dechex(ord(substr($s, $i, 1)));
  return  strtoupper($r);
 }        

  function encrypt($t) {
    $r = '';
    for($i=0;$i < strlen($t) % strlen($this->key); $i++)
       $d.=" ";
    for($i=0;$i<strlen($t);$i+=strlen($this->key))
      for($j = 0; $j < strlen($this->key); $j++)
       $r.=$t[$i+$j] ^ $this->key[$j]; 
    return base64_encode(utf8_encode($r));

  }
}
?>