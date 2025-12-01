package co.com.bancolombia.api;

import co.com.bancolombia.api.rest.restaurant.RestaurantApiRest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ApiRestTest {

    RestaurantApiRest apiRest = new RestaurantApiRest();

    @Test
    void apiRestTest() {
        var response = apiRest.commandName();
        assertEquals("", response);
    }
}
