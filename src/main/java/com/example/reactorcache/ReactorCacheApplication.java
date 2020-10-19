package com.example.reactorcache;


import reactor.core.Disposable;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ReactorCacheApplication {

public static void main(String[] args) throws InterruptedException {
    fluxConnectToSecondSubscriberWithoutUseCache();
    System.out.println("-- Intervalo entre retorno dos  flux --");
    //controla quando será chamado o cache
    fluxConnectToSecondSubscriberUsingCache();
  }

  private static void fluxConnectToSecondSubscriberWithoutUseCache() throws InterruptedException {
  //este primeiro nao utiliza cache, so "compartilha o flux"
    Flux<Long> startTheExecutionFlux = Flux.interval(Duration.ofSeconds(1)).share();

    Flux FluxOne = Flux.from(startTheExecutionFlux);
    //a cada cinco segundos é gerado um novo numero
    Disposable disposable = FluxOne.subscribe(out -> System.out.println("FluxOne value: " + out));
    new CountDownLatch(1).await(5, TimeUnit.SECONDS);

    Flux fluxTwo = Flux.from(startTheExecutionFlux);
    Disposable secondDisposable = fluxTwo.subscribe(out -> System.out.println("fluxTwo value: " + out));
    new CountDownLatch(1).await(5, TimeUnit.SECONDS);

    disposable.dispose();
    secondDisposable.dispose();
    //imprime ambos os valores sem uso de cache
  }

  private static void fluxConnectToSecondSubscriberUsingCache() throws InterruptedException {
  //aqui ja fazemos uso do operador de cache
    Flux<Long> startTheExecutionFlux = Flux.interval(Duration.ofSeconds(1)).share().cache();

    //segue o mesmo processo de criacao de numeros do anterior mas agora utiliza cache
    Flux FluxOne = Flux.from(startTheExecutionFlux);
    Disposable disposable = FluxOne.subscribe(out -> System.out.println("FluxOne value: " + out));
    new CountDownLatch(1).await(5, TimeUnit.SECONDS);

    Flux fluxTwo = startTheExecutionFlux.share();
    Disposable secondDisposable = fluxTwo.subscribe(out -> System.out.println("fluxTwo value: " + out));
    new CountDownLatch(1).await(5, TimeUnit.SECONDS);
//na primeira excecuao do fluxtwo ele inicialmente irá retornanr o valor anterior (em cache)
    disposable.dispose();
    secondDisposable.dispose();
  }

}
