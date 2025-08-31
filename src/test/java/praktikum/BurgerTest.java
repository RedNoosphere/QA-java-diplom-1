package praktikum;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(Parameterized.class)
public class BurgerTest {

    private Burger burger;

    @Mock
    private Bun mockBun;

    @Mock
    private Ingredient mockIngredient1;

    @Mock
    private Ingredient mockIngredient2;

    @Mock
    private Ingredient mockIngredient3;

    // Параметры для тестирования getPrice
    @Parameterized.Parameter(0)
    public float bunPrice;

    @Parameterized.Parameter(1)
    public List<Float> ingredientPrices;

    @Parameterized.Parameter(2)
    public float expectedTotalPrice;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        burger = new Burger();
    }

    // Параметризованные данные для теста getPrice
    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {100f, Arrays.asList(50f, 30f), 280f}, // 100*2 + 50 + 30 = 280
                {50f, Arrays.asList(20f), 120f},       // 50*2 + 20 = 120
                {80f, Arrays.asList(), 160f},          // 80*2 + 0 = 160
                {70f, Arrays.asList(10f, 15f, 25f), 190f} // 70*2 + 10+15+25 = 190
        });
    }

    @Test
    public void testSetBuns() {
        burger.setBuns(mockBun);
        assertEquals(mockBun, burger.bun);
    }

    @Test
    public void testAddIngredient() {
        burger.addIngredient(mockIngredient1);
        assertEquals(1, burger.ingredients.size());
        assertTrue(burger.ingredients.contains(mockIngredient1));
    }

    @Test
    public void testRemoveIngredient() {
        burger.ingredients.add(mockIngredient1);
        burger.ingredients.add(mockIngredient2);

        burger.removeIngredient(0);

        assertEquals(1, burger.ingredients.size());
        assertFalse(burger.ingredients.contains(mockIngredient1));
        assertTrue(burger.ingredients.contains(mockIngredient2));
    }

    @Test
    public void testMoveIngredient() {
        burger.ingredients.add(mockIngredient1);
        burger.ingredients.add(mockIngredient2);
        burger.ingredients.add(mockIngredient3);

        burger.moveIngredient(0, 2);

        assertEquals(3, burger.ingredients.size());
        assertEquals(mockIngredient2, burger.ingredients.get(0));
        assertEquals(mockIngredient3, burger.ingredients.get(1));
        assertEquals(mockIngredient1, burger.ingredients.get(2));
    }

    @Test
    public void testGetPrice() {
        // Настраиваем моки
        when(mockBun.getPrice()).thenReturn(bunPrice);

        burger.setBuns(mockBun);

        // Добавляем моки ингредиентов с соответствующими ценами
        for (float price : ingredientPrices) {
            Ingredient mockIngredient = mock(Ingredient.class);
            when(mockIngredient.getPrice()).thenReturn(price);
            burger.addIngredient(mockIngredient);
        }

        float actualPrice = burger.getPrice();
        assertEquals(expectedTotalPrice, actualPrice, 0.001f);

        // Проверяем, что методы моков были вызваны
        verify(mockBun, times(1)).getPrice();
        for (Ingredient ingredient : burger.ingredients) {
            verify(ingredient, times(1)).getPrice();
        }
    }

    @Test
    public void testGetReceipt() {
        // Настраиваем моки
        when(mockBun.getName()).thenReturn("white bun");
        when(mockBun.getPrice()).thenReturn(100f);

        when(mockIngredient1.getType()).thenReturn(IngredientType.SAUCE);
        when(mockIngredient1.getName()).thenReturn("hot sauce");
        when(mockIngredient1.getPrice()).thenReturn(50f);

        when(mockIngredient2.getType()).thenReturn(IngredientType.FILLING);
        when(mockIngredient2.getName()).thenReturn("cutlet");
        when(mockIngredient2.getPrice()).thenReturn(30f);

        // Собираем бургер
        burger.setBuns(mockBun);
        burger.addIngredient(mockIngredient1);
        burger.addIngredient(mockIngredient2);

        // Получаем чек
        String receipt = burger.getReceipt();

        // Проверяем содержимое чека
        assertTrue("Should contain bun name", receipt.contains("(==== white bun ====)"));
        assertTrue("Should contain sauce ingredient", receipt.contains("= sauce hot sauce ="));
        assertTrue("Should contain filling ingredient", receipt.contains("= filling cutlet ="));

        // Более гибкая проверка цены с использованием регулярного выражения
        assertTrue("Should contain total price around 280",
                Pattern.compile("Price:.*280").matcher(receipt).find());

        // Проверяем вызовы методов моков
        verify(mockBun, times(2)).getName(); // Два раза для верхней и нижней булочки
        verify(mockIngredient1, times(1)).getType();
        verify(mockIngredient1, times(1)).getName();
        verify(mockIngredient2, times(1)).getType();
        verify(mockIngredient2, times(1)).getName();
    }

    @Test
    public void testGetReceiptWithNoIngredients() {
        when(mockBun.getName()).thenReturn("black bun");
        when(mockBun.getPrice()).thenReturn(80f);

        burger.setBuns(mockBun);

        String receipt = burger.getReceipt();

        assertTrue("Should contain bun name", receipt.contains("(==== black bun ====)"));

        // Более гибкая проверка цены с использованием регулярного выражения
        assertTrue("Should contain price for bun only around 160",
                Pattern.compile("Price:.*160").matcher(receipt).find());

        // Проверяем базовую структуру чека без ингредиентов
        String[] lines = receipt.split("\r?\n");
        assertTrue("First line should contain bun", lines[0].contains("black bun"));
        assertTrue("Second line should contain bun", lines[lines.length - 3].contains("black bun"));
        assertTrue("Should contain price line", lines[lines.length - 1].startsWith("Price:"));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testRemoveIngredientWithInvalidIndex() {
        burger.removeIngredient(0); // Пустой список ингредиентов
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testMoveIngredientWithInvalidIndex() {
        burger.moveIngredient(0, 1); // Пустой список ингредиентов
    }

    @Test
    public void testGetPriceWithNoIngredients() {
        when(mockBun.getPrice()).thenReturn(50f);

        burger.setBuns(mockBun);

        float price = burger.getPrice();
        assertEquals(100f, price, 0.001f); // Только цена двух булочек
        verify(mockBun, times(1)).getPrice();
    }

    @Test
    public void testGetReceiptFormat() {
        when(mockBun.getName()).thenReturn("test bun");
        when(mockBun.getPrice()).thenReturn(10f);

        when(mockIngredient1.getType()).thenReturn(IngredientType.SAUCE);
        when(mockIngredient1.getName()).thenReturn("test sauce");
        when(mockIngredient1.getPrice()).thenReturn(5f);

        burger.setBuns(mockBun);
        burger.addIngredient(mockIngredient1);

        String receipt = burger.getReceipt();

        // Проверяем общую структуру с более гибкими проверками
        assertTrue(receipt.contains("(==== test bun ====)"));
        assertTrue(receipt.contains("= sauce test sauce ="));
        assertTrue("Should contain price around 25",
                Pattern.compile("Price:.*25").matcher(receipt).find());
    }

    @Test
    public void testGetReceiptContainsAllElements() {
        when(mockBun.getName()).thenReturn("test bun");

        when(mockIngredient1.getType()).thenReturn(IngredientType.SAUCE);
        when(mockIngredient1.getName()).thenReturn("ketchup");

        when(mockIngredient2.getType()).thenReturn(IngredientType.FILLING);
        when(mockIngredient2.getName()).thenReturn("cheese");

        burger.setBuns(mockBun);
        burger.addIngredient(mockIngredient1);
        burger.addIngredient(mockIngredient2);

        String receipt = burger.getReceipt();

        // Проверяем, что все основные элементы присутствуют
        assertTrue(receipt.contains("(==== test bun ====)"));
        assertTrue(receipt.contains("= sauce ketchup ="));
        assertTrue(receipt.contains("= filling cheese ="));
        assertTrue(receipt.contains("Price:"));
    }
}