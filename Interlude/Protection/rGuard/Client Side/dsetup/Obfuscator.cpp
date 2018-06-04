#include "obfuscator.h"

typedef char odecoded[4095];
odecoded obbuf[4];
unsigned short lastbuf = 0;

__forceinline char *obDecodeStr(char *inst)
{
    lastbuf++;
    if (lastbuf>3) lastbuf = 0;
    unsigned int i = 0;
    unsigned int db = 0;
    bool phase = true;
    unsigned short schar = 0;
    while (inst[i]!=(char)0)
    {
        if (phase) 
        {
            schar = 0;
            schar+=(((unsigned short)inst[i]) & 0x0F);
        }
        else
        {
            schar+=(((unsigned short)inst[i]) & 0x0F) * 16; 
            obbuf[lastbuf][db] = (char)schar;
            db++;
        }

        phase = !phase;
        i++;
    }
    obbuf[lastbuf][db] = (char)0;

    return obbuf[lastbuf];
}