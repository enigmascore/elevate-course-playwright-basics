import { Navigate, Route, Routes } from "react-router-dom";

import Layout from "./components/Layout";
import RequireAuth from "./components/RequireAuth";
import CookDetailPage from "./pages/CookDetailPage";
import CooksPage from "./pages/CooksPage";
import LoginPage from "./pages/LoginPage";
import RecipesPage from "./pages/RecipesPage";
import ShoppingListPage from "./pages/ShoppingListPage";

// Real URLs for every screen - the tests assert on them.
export default function App() {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route
        element={
          <RequireAuth>
            <Layout />
          </RequireAuth>
        }
      >
        <Route path="/recipes" element={<RecipesPage />} />
        <Route path="/cooks" element={<CooksPage />} />
        <Route path="/cooks/:id" element={<CookDetailPage />} />
        <Route path="/shopping-list" element={<ShoppingListPage />} />
      </Route>
      <Route path="*" element={<Navigate to="/recipes" replace />} />
    </Routes>
  );
}
