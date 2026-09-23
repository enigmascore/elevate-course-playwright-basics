import { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";

import { api, type Cook } from "../api";

export default function CookDetailPage() {
  const { id = "" } = useParams();
  const [cook, setCook] = useState<Cook | null>(null);
  const [missing, setMissing] = useState(false);

  useEffect(() => {
    let cancelled = false;
    api
      .cook(id)
      .then((result) => !cancelled && setCook(result))
      .catch(() => !cancelled && setMissing(true));
    return () => {
      cancelled = true;
    };
  }, [id]);

  if (missing) {
    return (
      <>
        <h1>No such cook</h1>
        <p>
          <Link to="/cooks">Back to cooks</Link>
        </p>
      </>
    );
  }

  if (!cook) return <p className="muted">Loading...</p>;

  return (
    <>
      <h1>{cook.name}</h1>
      <p>
        {cook.level === "PRO" ? "Professional" : "Home"} cook &middot; {cook.email}
      </p>
      <h2>Recipes</h2>
      {cook.recipes.length === 0 ? (
        <p className="muted">No recipes yet.</p>
      ) : (
        <ul>
          {cook.recipes.map((title) => (
            <li key={title}>{title}</li>
          ))}
        </ul>
      )}
      <p>
        <Link to="/cooks">Back to cooks</Link>
      </p>
    </>
  );
}
