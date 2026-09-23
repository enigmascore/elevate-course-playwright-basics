import { NavLink, Outlet, useNavigate } from "react-router-dom";

import { clearSession, readSession } from "../session";

export default function Layout() {
  const navigate = useNavigate();
  const session = readSession();

  function logout() {
    // a NATIVE confirm - handled in tests with page.on("dialog")
    if (window.confirm("Log out of Recipe Box?")) {
      clearSession();
      void navigate("/login");
    }
  }

  return (
    <>
      <header className="site">
        <a className="brand" href="/recipes">
          Recipe Box
        </a>
        <nav aria-label="Main">
          <NavLink to="/recipes">Recipes</NavLink>
          <NavLink to="/cooks">Cooks</NavLink>
          <NavLink to="/shopping-list">Shopping list</NavLink>
        </nav>
        <span>{session?.name}</span>
        <button type="button" onClick={logout}>
          Log out
        </button>
      </header>
      <main>
        <Outlet />
      </main>
    </>
  );
}
