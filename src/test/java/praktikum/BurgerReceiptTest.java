package praktikum;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.regex.Pattern;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class BurgerReceiptTest {

    private Burger burger;

    @Mock
    private Bun mockBun;

    @Mock
    private Ingredient mockIngredientSauce;

    @Mock
    private Ingredient mockIngredientFilling;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        burger = new Burger();
    }

    @Test
    public void testGetReceiptContainsBunName() {
        when(mockBun.getName()).thenReturn("white bun");
        burger.setBuns(mockBun);

        String receipt = burger.getReceipt();
        assertTrue(receipt.contains("(==== white bun ====)"));
    }

    @Test
    public void testGetReceiptContainsSauceIngredient() {
        configureMocksForReceipt();
        burger.setBuns(mockBun);
        burger.addIngredient(mockIngredientSauce);

        String receipt = burger.getReceipt();
        assertTrue(receipt.contains("= sauce hot sauce ="));
    }

    @Test
    public void testGetReceiptContainsFillingIngredient() {
        configureMocksForReceipt();
        burger.setBuns(mockBun);
        burger.addIngredient(mockIngredientFilling);

        String receipt = burger.getReceipt();
        assertTrue(receipt.contains("= filling cutlet ="));
    }

    @Test
    public void testGetReceiptContainsTotalPrice() {
        configureMocksForReceipt();
        burger.setBuns(mockBun);
        burger.addIngredient(mockIngredientSauce);
        burger.addIngredient(mockIngredientFilling);

        String receipt = burger.getReceipt();
        assertTrue(Pattern.compile("Price:.*280").matcher(receipt).find());
    }

    @Test
    public void testGetReceiptWithNoIngredientsContainsPrice() {
        when(mockBun.getName()).thenReturn("black bun");
        when(mockBun.getPrice()).thenReturn(80f);
        burger.setBuns(mockBun);

        String receipt = burger.getReceipt();
        assertTrue(Pattern.compile("Price:.*160").matcher(receipt).find());
    }

    @Test
    public void testGetReceiptCallsBunNameTwice() {
        when(mockBun.getName()).thenReturn("test bun");
        burger.setBuns(mockBun);

        burger.getReceipt();
        verify(mockBun, times(2)).getName();
    }

    private void configureMocksForReceipt() {
        when(mockBun.getName()).thenReturn("white bun");
        when(mockBun.getPrice()).thenReturn(100f);

        when(mockIngredientSauce.getType()).thenReturn(IngredientType.SAUCE);
        when(mockIngredientSauce.getName()).thenReturn("hot sauce");
        when(mockIngredientSauce.getPrice()).thenReturn(50f);

        when(mockIngredientFilling.getType()).thenReturn(IngredientType.FILLING);
        when(mockIngredientFilling.getName()).thenReturn("cutlet");
        when(mockIngredientFilling.getPrice()).thenReturn(30f);
    }
}