#ifndef _OBFUSCATOR_H
#define _OBFUSCATOR_H

#ifdef X
#pragma message("MOCROS IS NOT FINDED! RECALLBACK ERROR!")
#endif

#ifdef DO_OBFUSCATE_STRINGS

__forceinline char *obDecodeStr(char *inst);

#define X(s)obDecodeStr(OBPREPROCESSENCODEDSTROBFUSCATION(s))
#else
#define X(s)s
#endif

#endif