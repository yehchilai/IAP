package com.iamhomebody.iap;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import android.util.Log;

public class JavaSSLHelper
{
    //see https://developer.android.com/training/articles/security-ssl.html
    public static void trust(byte[] crtFileContent)
    {
        try
        {
            // Load CAs from an InputStream
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            InputStream caInput = new BufferedInputStream(new ByteArrayInputStream(crtFileContent));
            Certificate ca;
            try {
                ca = cf.generateCertificate(caInput);
//                Log.d("JavaSSLHelper", "ca=" + ((X509Certificate) ca).getSubjectDN());
                Log.d("JavaSSLHelper", "Certificate successfully created");
            } finally
            {
                caInput.close();
            }
            // Create a KeyStore containing our trusted CAs
            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);
            // Create a TrustManager that trusts the CAs in our KeyStore
            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);
            try
            {
                // Create an SSLContext that uses our TrustManager
                SSLContext context = SSLContext.getInstance("TLS");
                context.init(null, tmf.getTrustManagers(), null);
                //this is important: unity will use the default ssl socket factory we just created
                HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
                Log.d("JavaSSLHelper", "Default SSL Socket set.");
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            } catch (KeyManagementException e) {
                throw new RuntimeException(e);
            }
        }catch(Exception e)
        {
            throw new RuntimeException(e);
        }
    }
}
