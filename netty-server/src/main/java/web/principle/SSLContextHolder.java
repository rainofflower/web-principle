package web.principle;

import io.netty.handler.ssl.ClientAuth;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;

import javax.net.ssl.SSLException;
import java.io.File;

/**
 * ${DESCRIPTION}
 *
 * @author yanghui
 * @date 2020-09-16 12:33
 **/
public class SSLContextHolder {

    public static SslContext serverSslCtx;

    public static SslContext clientSslCtx;

    static{
        try {
            File certChainFile = new File("C:/Program Files/OpenSSL-Win64/bin/server.crt");
            File keyFile = new File("C:/Program Files/OpenSSL-Win64/bin/pkcs8_server.key");
            File rootFile = new File("C:/Program Files/OpenSSL-Win64/bin/ca.crt");
            serverSslCtx = SslContextBuilder
                    .forServer(certChainFile, keyFile)
                    .trustManager(rootFile)
                    .clientAuth(ClientAuth.REQUIRE)
                    .build();


            File clientCertChainFile = new File("C:/Program Files/OpenSSL-Win64/bin/client.crt");
            File clientKeyFile = new File("C:/Program Files/OpenSSL-Win64/bin/pkcs8_client.key");
            File clientRootFile = new File("C:/Program Files/OpenSSL-Win64/bin/ca.crt");
            // Configure SSL.
            clientSslCtx = SslContextBuilder
                    .forClient()
                    .keyManager(clientCertChainFile, clientKeyFile)
                    .trustManager(clientRootFile)
                    .build();
        } catch (SSLException e) {
            e.printStackTrace();
        }
    }

}
