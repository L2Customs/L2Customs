#include "BlowFish.h"

unsigned int Blowfish_F(struct Blowfish_CTX *ctx, unsigned int x)
{
	unsigned short int a, b, c, d;
	unsigned int  y;

	d = (unsigned short int)(x & 0xFF);
	x >>= 8;
	c = (unsigned short int)(x & 0xFF);
	x >>= 8;
	b = (unsigned short int)(x & 0xFF);
	x >>= 8;
	a = (unsigned short int)(x & 0xFF);

	y = ctx->S[0][a] + ctx->S[1][b];

	y ^= ctx->S[2][c];
	y += ctx->S[3][d];

   return y;
}


void Blowfish_EncryptBlock(struct Blowfish_CTX *ctx, unsigned int *xl, unsigned int *xr)
{
	unsigned int Xl, Xr, temp;
	short int i;

	Xl = *xl;
	Xr = *xr;

	for (i = 0; i < N; ++i)
	{
		Xl ^= ctx->P[i];
		Xr = Blowfish_F(ctx, Xl) ^ Xr;
		
		temp = Xl;
		Xl = Xr;
		Xr = temp;
	}

	temp = Xl;
	Xl = Xr;
	Xr = temp;
	
	Xr ^= ctx->P[N];
	Xl ^= ctx->P[N + 1];
	
	*xl = Xl;
	*xr = Xr;
}

void Blowfish_DecryptBlock(struct Blowfish_CTX *ctx, unsigned int *xl, unsigned int *xr)
{
	unsigned int Xl, Xr, temp;
	short int i;

	Xl = *xl;
	Xr = *xr;

	for (i = N + 1; i > 1; --i)
	{
		Xl ^= ctx->P[i];
		Xr = Blowfish_F(ctx, Xl) ^ Xr;

		temp = Xl;
		Xl = Xr;
		Xr = temp;
	}

	temp = Xl;
	Xl = Xr;
	Xr = temp;

	Xr ^= ctx->P[1];
	Xl ^= ctx->P[0];

	*xl = Xl;
	*xr = Xr;
}


void Blowfish_Init(struct Blowfish_CTX *ctx, unsigned char *key, int keyLen)
{
	int i, j, k;
	unsigned int data, datal, datar;
	
	for (i = 0; i < 4; i++)
	{
		for (j = 0; j < 256; j++)
			ctx->S[i][j] = OrigS[i][j];
	}
	
	j = 0;
	
	for (i = 0; i < N + 2; ++i)
	{
		data = 0;
		for (k = 0; k < 4; ++k)
		{
			data = (data << 8) | key[j];
			j = j + 1;

			if (j >= keyLen)
				j = 0;
		}
		
		ctx->P[i] = OrigP[i] ^ data;
	}

	datal = 0;
	datar = 0;

	for (i = 0; i < N + 2; i += 2)
	{
		Blowfish_EncryptBlock(ctx, &datal, &datar);
		ctx->P[i] = datal;
		ctx->P[i + 1] = datar;
	}

	for (i = 0; i < 4; ++i)
	{
		for (j = 0; j < 256; j += 2)
		{
			Blowfish_EncryptBlock(ctx, &datal, &datar);
			ctx->S[i][j] = datal;
			ctx->S[i][j + 1] = datar;
		}
	}
}

void Blowfish_Encrypt(struct Blowfish_CTX *ctx, unsigned char *data, int dataLen)
{
	for (int i = 0; i <= dataLen / 8; i++)
		Blowfish_EncryptBlock(ctx, (unsigned int*) (data + i * 8), (unsigned int*) (data + i * 8 + 4));
}

void Blowfish_Decrypt(struct Blowfish_CTX *ctx, unsigned char *data, int dataLen)
{
	for (int i = 0; i <= dataLen / 8; i++)
		Blowfish_DecryptBlock(ctx, (unsigned int*) (data + i * 8), (unsigned int*) (data + i * 8 + 4));
}