import { type FormEvent, useCallback, useEffect, useRef, useState } from "react";

import { api, ApiError, type Cook, type FieldErrors, type Recipe } from "../api";
import FieldError from "../components/FieldError";

const CUISINES = ["Italian", "Japanese", "Indian", "Mexican", "French"];

// mirrors the backend's RecipeValidator rules
function validate(title: string, cuisine: string, cookId: number | null, servings: number): FieldErrors {
  const errors: FieldErrors = {};
  if (!title.trim()) errors.title = "Title is required";
  if (!cuisine) errors.cuisine = "Choose a cuisine";
  if (cookId === null) errors.cookId = "Choose a cook";
  if (!Number.isInteger(servings) || servings < 1 || servings > 24) errors.servings = "Servings must be between 1 and 24";
  return errors;
}

export default function RecipesPage() {
  const [query, setQuery] = useState("");
  const [recipes, setRecipes] = useState<Recipe[]>([]);
  const [cooks, setCooks] = useState<Cook[]>([]);
  const latestRequest = useRef(0);

  const [title, setTitle] = useState("");
  const [cuisine, setCuisine] = useState("");
  const [cookId, setCookId] = useState<number | null>(null);
  const [servings, setServings] = useState(4);
  const [vegetarian, setVegetarian] = useState(false);
  const [errors, setErrors] = useState<FieldErrors>({});
  const [notice, setNotice] = useState<string | null>(null);

  const [toDelete, setToDelete] = useState<Recipe | null>(null);

  // server-side search: every keystroke is a real GET /api/recipes?q=...; the
  // counter drops answers that arrive out of order
  const load = useCallback(async (q: string) => {
    const requestNumber = ++latestRequest.current;
    const result = await api.recipes(q);
    if (requestNumber === latestRequest.current) setRecipes(result);
  }, []);

  useEffect(() => {
    void load(query);
  }, [query, load]);

  useEffect(() => {
    void api.cooks().then(setCooks);
  }, []);

  async function addRecipe(event: FormEvent) {
    event.preventDefault();
    setNotice(null);
    const clientErrors = validate(title, cuisine, cookId, servings);
    setErrors(clientErrors);
    if (Object.keys(clientErrors).length > 0) return;

    try {
      const created = await api.addRecipe({ title, cuisine, cookId, servings, vegetarian });
      setTitle("");
      setCuisine("");
      setCookId(null);
      setServings(4);
      setVegetarian(false);
      setNotice(`Added "${created.title}"`);
      await load(query);
    } catch (e) {
      if (e instanceof ApiError && e.errors) setErrors(e.errors);
      else setErrors({ form: "Something went wrong - please try again" });
    }
  }

  async function confirmDelete() {
    if (!toDelete) return;
    await api.deleteRecipe(toDelete.id);
    setToDelete(null);
    await load(query);
  }

  return (
    <>
      <h1>Recipes</h1>

      <div className="field">
        <label htmlFor="search">Search</label>
        <input
          id="search"
          type="search"
          placeholder="Title or cuisine"
          value={query}
          onChange={(e) => setQuery(e.target.value)}
        />
      </div>

      <table>
        <thead>
          <tr>
            <th>Title</th>
            <th>Cuisine</th>
            <th>Cook</th>
            <th>Serves</th>
            <th>Vegetarian</th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          {recipes.map((recipe) => (
            <tr key={recipe.id} data-testid="recipe-row">
              <td>{recipe.title}</td>
              <td>{recipe.cuisine}</td>
              <td>{recipe.cookName}</td>
              <td>{recipe.servings}</td>
              <td>{recipe.vegetarian ? "Yes" : "No"}</td>
              <td>
                <button
                  type="button"
                  className="secondary"
                  aria-label={`Delete ${recipe.title}`}
                  onClick={() => setToDelete(recipe)}
                >
                  Delete
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
      {recipes.length === 0 && <p className="muted">No recipes match.</p>}

      <h2>Add recipe</h2>
      <form className="card" onSubmit={addRecipe} noValidate>
        <div className="field">
          <label htmlFor="title">Title</label>
          <input id="title" type="text" value={title} onChange={(e) => setTitle(e.target.value)} aria-describedby="title-error" />
          <FieldError id="title-error" message={errors.title} />
        </div>
        <div className="field">
          <label htmlFor="cuisine">Cuisine</label>
          <select id="cuisine" value={cuisine} onChange={(e) => setCuisine(e.target.value)} aria-describedby="cuisine-error">
            <option value="">Choose...</option>
            {CUISINES.map((c) => (
              <option key={c} value={c}>
                {c}
              </option>
            ))}
          </select>
          <FieldError id="cuisine-error" message={errors.cuisine} />
        </div>
        <div className="field">
          <label htmlFor="cook">Cook</label>
          <select
            id="cook"
            value={cookId ?? ""}
            onChange={(e) => setCookId(e.target.value ? Number(e.target.value) : null)}
            aria-describedby="cook-error"
          >
            <option value="">Choose...</option>
            {cooks.map((cook) => (
              <option key={cook.id} value={cook.id}>
                {cook.name}
              </option>
            ))}
          </select>
          <FieldError id="cook-error" message={errors.cookId} />
        </div>
        <div className="field">
          <label htmlFor="servings">Servings</label>
          <input
            id="servings"
            type="number"
            min={1}
            max={24}
            value={servings}
            onChange={(e) => setServings(Number(e.target.value))}
            aria-describedby="servings-error"
          />
          <FieldError id="servings-error" message={errors.servings} />
        </div>
        <div className="field inline">
          <input id="vegetarian" type="checkbox" checked={vegetarian} onChange={(e) => setVegetarian(e.target.checked)} />
          <label htmlFor="vegetarian">Vegetarian</label>
        </div>
        <FieldError id="form-error" message={errors.form} />
        {notice && <p className="notice">{notice}</p>}
        <button type="submit">Add recipe</button>
      </form>

      {toDelete && (
        <div className="backdrop">
          <div className="modal" role="dialog" aria-modal="true" aria-labelledby="delete-title">
            <h2 id="delete-title">Delete recipe?</h2>
            <p>Remove &ldquo;{toDelete.title}&rdquo; from the box?</p>
            <div className="actions">
              <button type="button" className="secondary" onClick={() => setToDelete(null)}>
                Cancel
              </button>
              <button type="button" className="danger" onClick={confirmDelete}>
                Delete
              </button>
            </div>
          </div>
        </div>
      )}
    </>
  );
}
