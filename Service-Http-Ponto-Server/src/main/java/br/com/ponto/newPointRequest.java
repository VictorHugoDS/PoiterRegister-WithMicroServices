package br.com.ponto;

import br.com.ponto.producer.KafkaDispatcher;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class newPointRequest extends HttpServlet {

    private final KafkaDispatcher<UserRequest> kafkaDispatcher = new KafkaDispatcher<>(
            "PONTO_NEW_REQUEST",
            Collections.emptyMap(),
            newPointRequest.class.getSimpleName()
    );

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)  {
        var name = req.getParameter("name");
        var cpf = req.getParameter("cpf");

        var id = UUID.randomUUID().toString();
        var type = UserRequest.class.getName();
        var payload = new UserRequest(name, cpf);
        try {
            kafkaDispatcher.send(cpf, type, id, payload);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void destroy() {
        kafkaDispatcher.close();
        super.destroy();
    }
}
