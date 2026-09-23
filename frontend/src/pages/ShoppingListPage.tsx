import { useEffect, useState } from "react";

import { api, type ShoppingItem } from "../api";

// The backend answers this list SLOWLY on purpose ( ~1.5 s ): the page shows a
// loading state first, so a test has something real to wait for.
export default function ShoppingListPage() {
  const [items, setItems] = useState<ShoppingItem[] | null>(null);

  useEffect(() => {
    let cancelled = false;
    void api.shoppingList().then((result) => !cancelled && setItems(result));
    return () => {
      cancelled = true;
    };
  }, []);

  return (
    <>
      <h1>Shopping list</h1>
      {items === null ? (
        <p className="muted">Loading shopping list...</p>
      ) : (
        <table>
          <thead>
            <tr>
              <th>Item</th>
              <th>Quantity</th>
              <th>For</th>
            </tr>
          </thead>
          <tbody>
            {items.map((item) => (
              <tr key={item.id} data-testid="shopping-row">
                <td>{item.item}</td>
                <td>{item.quantity}</td>
                <td>{item.forRecipe}</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </>
  );
}
