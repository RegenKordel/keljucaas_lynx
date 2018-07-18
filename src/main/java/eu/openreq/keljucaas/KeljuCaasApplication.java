package eu.openreq.keljucaas;

import org.springframework.boot.SpringApplication;

public class KeljuCaasApplication {
	
	public static void main(String[] args) {
		System.setProperty("org.apache.tomcat.util.buf.UDecoder.ALLOW_ENCODED_SLASH", "true");
		SpringApplication.run(KeljuCaasApplication.class, args);

	}

}
