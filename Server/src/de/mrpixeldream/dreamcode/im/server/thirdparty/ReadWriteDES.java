package de.mrpixeldream.dreamcode.im.server.thirdparty;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.SecretKeySpec;

public class ReadWriteDES
{
	public static void encode( byte[] bytes, OutputStream out, String pass ) throws Exception
	{
	    Cipher c = Cipher.getInstance( "DES" );
	    Key k = new SecretKeySpec( pass.getBytes(), "DES" );
	    c.init( Cipher.ENCRYPT_MODE, k );

	    OutputStream cos = new CipherOutputStream( out, c );
	    cos.write( bytes );
	    cos.flush();
	    cos.close();
	}

	public static byte[] decode( InputStream is, String pass ) throws Exception
	{
	    Cipher c = Cipher.getInstance( "DES" );
	    Key k = new SecretKeySpec( pass.getBytes(), "DES" );
	    c.init( Cipher.DECRYPT_MODE, k );

	    ByteArrayOutputStream bos = new ByteArrayOutputStream();
	    CipherInputStream cis = new CipherInputStream( is, c );

	    for ( int b; (b = cis.read()) != -1; )
	      bos.write( b );

	    cis.close();
	    return bos.toByteArray();
	}
}
