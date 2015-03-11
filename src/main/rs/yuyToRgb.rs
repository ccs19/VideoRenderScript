#pragma version(1)
#pragma rs java_package_name(com.sbullet.videorenderscript)

rs_allocation gIn;

int width;
int height;
int frameSize;

int table[5*256];

void init(){
        for(int i = 0; i < 256; i++) {
            table[0+i] = 1192 * (i - 16);
            if(table[0+i] < 0) {
                table[0+i] = 0;
            }

            table[1*256 + i] = 1634 * (i - 128);
            table[2*256 + i] = 833 * (i - 128);
            table[3*256 + i] = 400 * (i - 128);
            table[4*256 + i] = 2066 * (i - 128);
        }
}

void root( const void *v_in, uchar4 *v_out, const void *usr, uint32_t x, uint32_t y) {



    int index = (x & (~1)) + (( y>>1) * width );
    int y1 = (int) (rsGetElementAt_uchar(gIn, x, y));
    int u = (int)( rsGetElementAt_uchar(gIn, index ));
    int y2 = (int)( rsGetElementAt_uchar(gIn, index+1));
    int v = (int)( rsGetElementAt_uchar(gIn, index+2));

    int y1192_1 = table[y1];
    int r1 = (y1192_1 + table[1*256+v]) >> 10;
    int g1 = (y1192_1 - table[2*256+v] - table[3*256+u]) >> 10;
    int b1 = (y1192_1 + table[4*256+u]) >> 10;

    int y1192_2 = table[y2];
    int r2 = (y1192_2 + table[1*256+v]) >> 10;
    int g2 = (y1192_2 - table[2*256+v] - table[3*256+u]) >> 10;
    int b2 = (y1192_2 + table[4*256+u]) >> 10;

    r1 = r1 > 255 ? 255 : r1 < 0 ? 0 : r1;
    g1 = g1 > 255 ? 255 : g1 < 0 ? 0 : g1;
    b1 = b1 > 255 ? 255 : b1 < 0 ? 0 : b1;
    r2 = r2 > 255 ? 255 : r2 < 0 ? 0 : r2;
    g2 = g2 > 255 ? 255 : g2 < 0 ? 0 : g2;
    b2 = b2 > 255 ? 255 : b2 < 0 ? 0 : b2;

    uchar4 res4, res8;
    res4.r = (uchar)r1;
    res4.g = (uchar)g1;
    res4.b = (uchar)b1;
    res4.a = 0xFF;

    res8.r = (uchar)r2;
    res8.g = (uchar)g2;
    res8.b = (uchar)b2;
    res8.a = 0xFF;

    *v_out++ = res4;
    *v_out = res8;

}

