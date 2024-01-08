package com.estoquepreco.estoquepreco.connections;

import com.estoquepreco.estoquepreco.consts.RabbitConsts;
import jakarta.annotation.PostConstruct;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.stereotype.Component;



@Component
public class RabbitMQConnection {
    private AmqpAdmin amqpAdmin;
    public RabbitMQConnection(AmqpAdmin amqpAdmin){
        this.amqpAdmin = amqpAdmin;
    }

    private static final String NOME_EXCHANGE = "amq.direct";
    private Queue fila(String nomeFila){
        return new Queue(nomeFila, true, false, false);
    }
    private DirectExchange trocaDireta(){
        return new DirectExchange(NOME_EXCHANGE);
    }

    private Binding relacionamento(Queue fila, DirectExchange troca){
       return new Binding(fila.getName(), Binding.DestinationType.EXCHANGE, troca.getName(), fila.getName(), null);
    }

    // Mesmo assim, precisamos que esse método seja chamado, como fazemos isso? Utilizando a anotação abaixo:
    @PostConstruct
    //Com essa anotação ele vai executar oq está dentro do método, criando nossas filas!
    private void adiciona(){
        Queue filaEstoque = this.fila(RabbitConsts.FILA_ESTOQUE);
        Queue filaPreco = this.fila(RabbitConsts.FILA_PRECO);

        DirectExchange troca = this.trocaDireta();

        Binding ligEstq = this.relacionamento(filaEstoque, troca);
        Binding ligPreco = this.relacionamento(filaPreco, troca);

        // Aqui criamos as filas
        this.amqpAdmin.declareQueue(filaEstoque);
        this.amqpAdmin.declareQueue(filaPreco);

        // Aqui criamos as exchanges
        this.amqpAdmin.declareExchange(troca);

        // Aqui criamos o binding
        this.amqpAdmin.declareBinding(ligEstq);
        this.amqpAdmin.declareBinding(ligPreco);
    }

}
