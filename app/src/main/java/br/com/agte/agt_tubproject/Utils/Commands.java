package br.com.agte.agt_tubproject.Utils;

public class Commands {

    public static final byte[] engine = { (byte)0x4D }; // + VEng
    public static final byte[] color = { (byte) 0xCC }; // + NLD + VCr + VCg + VCb
    public static final byte[] color_all = { (byte) 0xCA }; // + NLDS + VCr + VCg + VCb
    public static final byte[] strip = { (byte) 0xCF }; // + NLDS + VCr + VCg + VCb
    public static final byte[] color_r = { (byte)0x52 }; // + VCr
    public static final byte[] color_g = { (byte)0x47 }; // + VCg
    public static final byte[] color_b = { (byte)0x42 }; // + VCb
    public static final byte[] temperature = { (byte)0x54 }; // + VTemp

}
