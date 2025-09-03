package praktikum;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class BurgerTest {

    private Burger burger;

    @Mock
    private Bun mockBun;

    @Mock
    private Ingredient mockIngredientSauce;

    @Mock
    private Ingredient mockIngredientFilling;

    @Mock
    private Ingredient mockIngredientThird;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        burger = new Burger();
    }

    @Test
    public void testSetBuns() {
        burger.setBuns(mockBun);
        assertEquals(mockBun, burger.bun);
    }

    @Test
    public void testAddIngredientIncreasesSize() {
        burger.addIngredient(mockIngredientSauce);
        assertEquals(1, burger.ingredients.size());
    }

    @Test
    public void testAddIngredientContainsAddedIngredient() {
        burger.addIngredient(mockIngredientSauce);
        assertTrue(burger.ingredients.contains(mockIngredientSauce));
    }

    @Test
    public void testRemoveIngredientDecreasesSize() {
        burger.ingredients.add(mockIngredientSauce);
        burger.ingredients.add(mockIngredientFilling);

        burger.removeIngredient(0);
        assertEquals(1, burger.ingredients.size());
    }

    @Test
    public void testRemoveIngredientRemovesCorrectIngredient() {
        burger.ingredients.add(mockIngredientSauce);
        burger.ingredients.add(mockIngredientFilling);

        burger.removeIngredient(0);
        assertFalse(burger.ingredients.contains(mockIngredientSauce));
    }

    @Test
    public void testRemoveIngredientKeepsOtherIngredients() {
        burger.ingredients.add(mockIngredientSauce);
        burger.ingredients.add(mockIngredientFilling);

        burger.removeIngredient(0);
        assertTrue(burger.ingredients.contains(mockIngredientFilling));
    }

    @Test
    public void testMoveIngredientChangesOrder() {
        burger.ingredients.add(mockIngredientSauce);
        burger.ingredients.add(mockIngredientFilling);
        burger.ingredients.add(mockIngredientThird);

        burger.moveIngredient(0, 2);

        assertEquals(mockIngredientFilling, burger.ingredients.get(0));
        assertEquals(mockIngredientThird, burger.ingredients.get(1));
        assertEquals(mockIngredientSauce, burger.ingredients.get(2));
    }

    @Test
    public void testMoveIngredientMaintainsSize() {
        burger.ingredients.add(mockIngredientSauce);
        burger.ingredients.add(mockIngredientFilling);

        burger.moveIngredient(0, 1);
        assertEquals(2, burger.ingredients.size());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testRemoveIngredientWithInvalidIndexThrowsException() {
        burger.removeIngredient(0);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testMoveIngredientWithInvalidIndexThrowsException() {
        burger.moveIngredient(0, 1);
    }

    @Test
    public void testGetPriceWithNoIngredientsReturnsBunPriceOnly() {
        when(mockBun.getPrice()).thenReturn(50f);
        burger.setBuns(mockBun);

        float price = burger.getPrice();
        assertEquals(100f, price, 0.001f);
        verify(mockBun, times(1)).getPrice();
    }
}