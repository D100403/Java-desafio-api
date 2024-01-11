package ejercicio5api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
@RequestMapping("/api/placeholder")
public class JsonPlaceholderController {

    private final ApiDataRepository apiDataRepository;
    private final ObjectMapper objectMapper;

    @Autowired
    public JsonPlaceholderController(ApiDataRepository apiDataRepository, ObjectMapper objectMapper) {
        this.apiDataRepository = apiDataRepository;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/getData")
    @ResponseBody
    public String getJsonPlaceholderData() {
        try {
            // Verificar si ya existe un registro en la base de datos
            ApiDataEntity existingData = apiDataRepository.findById(3L).orElse(null);

            if (existingData != null) {
                // Si existe, convierte el campo asociado de texto a objeto usando Jackson
                JsonNode jsonDataNode = objectMapper.readTree(existingData.getJsonData());
                return jsonDataNode.toString();
            } else {
                // Si no existe, invoca a la API rest y obtén la respuesta
                // Supongamos que tienes un método para obtener datos de la API
                String apiResponse = getJsonPlaceholderDataFromApi();

                // Persistir en la base de datos
                ApiDataEntity newData = new ApiDataEntity();
                newData.setJsonData(apiResponse);
                apiDataRepository.save(newData);

                return apiResponse;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Error al procesar los datos";
        }
    }

    // Método para simular obtener datos de la API de JSON Placeholder
    private String getJsonPlaceholderDataFromApi() {
        // Aquí puedes realizar la lógica para obtener datos de la API de JSON Placeholder
        // Puedes utilizar RestTemplate o WebClient de Spring para realizar la solicitud HTTP
        // Supongamos que estás simulando la respuesta
        return "{\"placeholderKey\": \"placeholderValue\"}";
    }
}
