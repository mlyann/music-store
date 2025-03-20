package la1;

import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.*;


class NavigationStateTest {

    @Test
    void testEnumConstants() {
        NavigationState[] states = NavigationState.values();
        assertNotNull(states);
        // 检查里面是否有MAIN_MENU, SEARCH_MENU等
        assertTrue(states.length >= 1);
        assertNotNull(NavigationState.valueOf("MAIN_MENU"));
        // ...
    }

    @Test
    void testEnumToString() {
        assertEquals("MAIN_MENU", NavigationState.MAIN_MENU.name());
    }
}

