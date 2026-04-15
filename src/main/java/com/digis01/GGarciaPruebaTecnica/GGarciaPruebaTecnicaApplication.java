package com.digis01.GGarciaPruebaTecnica;

import java.util.Date;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GGarciaPruebaTecnicaApplication {

	public static void main(String[] args) {
		SpringApplication.run(GGarciaPruebaTecnicaApplication.class, args);
                Date fecha = new Date();
                System.out.println(fecha.getTime());
	}

}
