import { type FormEvent, useCallback, useEffect, useState } from "react";
import { Link } from "react-router-dom";

import { api, ApiError, type Cook, type FieldErrors } from "../api";
import FieldError from "../components/FieldError";

const EMAIL = /^[^@\s]+@[^@\s]+\.[^@\s]+$/;

// mirrors the backend's CookValidator rules
function validate(name: string, email: string, level: string): FieldErrors {
  const errors: FieldErrors = {};
  if (!name.trim()) errors.name = "Name is required";
  if (!email.trim()) errors.email = "Email is required";
  else if (!EMAIL.test(email.trim())) errors.email = "Email must look like name@example.com";
  if (!level) errors.level = "Choose a level";
  return errors;
}

// The cooks area - what the course's graded task tests.
export default function CooksPage() {
  const [cooks, setCooks] = useState<Cook[]>([]);
  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [level, setLevel] = useState("");
  const [errors, setErrors] = useState<FieldErrors>({});
  const [notice, setNotice] = useState<string | null>(null);
  const [toRemove, setToRemove] = useState<Cook | null>(null);
  const [removeError, setRemoveError] = useState<string | null>(null);

  const load = useCallback(async () => setCooks(await api.cooks()), []);

  useEffect(() => {
    let cancelled = false;
    void api.cooks().then((result) => !cancelled && setCooks(result));
    return () => {
      cancelled = true;
    };
  }, []);

  async function addCook(event: FormEvent) {
    event.preventDefault();
    setNotice(null);
    const clientErrors = validate(name, email, level);
    setErrors(clientErrors);
    if (Object.keys(clientErrors).length > 0) return;

    try {
      const created = await api.addCook({ name, email, level });
      setName("");
      setEmail("");
      setLevel("");
      setNotice(`Added ${created.name} - a welcome email is on its way`);
      await load();
    } catch (e) {
      if (e instanceof ApiError && e.errors) setErrors(e.errors);
      else setErrors({ form: "Something went wrong - please try again" });
    }
  }

  function openRemove(cook: Cook) {
    setRemoveError(null);
    setToRemove(cook);
  }

  async function confirmRemove() {
    if (!toRemove) return;
    try {
      await api.removeCook(toRemove.id);
      setToRemove(null);
      await load();
    } catch (e) {
      // a cook who still has recipes cannot be removed: the backend says so
      setRemoveError(e instanceof ApiError ? e.message : "Something went wrong - please try again");
    }
  }

  return (
    <>
      <h1>Cooks</h1>

      <table>
        <thead>
          <tr>
            <th>Name</th>
            <th>Email</th>
            <th>Level</th>
            <th>Recipes</th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          {cooks.map((cook) => (
            <tr key={cook.id} data-testid="cook-row">
              <td>
                <Link to={`/cooks/${cook.id}`}>{cook.name}</Link>
              </td>
              <td>{cook.email}</td>
              <td>{cook.level === "PRO" ? "Pro" : "Home"}</td>
              <td>{cook.recipeCount}</td>
              <td>
                <button type="button" className="secondary" aria-label={`Remove ${cook.name}`} onClick={() => openRemove(cook)}>
                  Remove
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>

      <h2>Add cook</h2>
      <form className="card" onSubmit={addCook} noValidate>
        <div className="field">
          <label htmlFor="name">Name</label>
          <input id="name" type="text" value={name} onChange={(e) => setName(e.target.value)} aria-describedby="name-error" />
          <FieldError id="name-error" message={errors.name} />
        </div>
        <div className="field">
          <label htmlFor="email">Email</label>
          <input id="email" type="email" value={email} onChange={(e) => setEmail(e.target.value)} aria-describedby="email-error" />
          <FieldError id="email-error" message={errors.email} />
        </div>
        <fieldset className="field" aria-describedby="level-error">
          <legend>Level</legend>
          <div className="field inline">
            <input id="level-home" type="radio" name="level" value="HOME" checked={level === "HOME"} onChange={() => setLevel("HOME")} />
            <label htmlFor="level-home">Home</label>
          </div>
          <div className="field inline">
            <input id="level-pro" type="radio" name="level" value="PRO" checked={level === "PRO"} onChange={() => setLevel("PRO")} />
            <label htmlFor="level-pro">Pro</label>
          </div>
          <FieldError id="level-error" message={errors.level} />
        </fieldset>
        <FieldError id="form-error" message={errors.form} />
        {notice && <p className="notice">{notice}</p>}
        <button type="submit">Add cook</button>
      </form>

      {toRemove && (
        <div className="backdrop">
          <div className="modal" role="dialog" aria-modal="true" aria-labelledby="remove-title">
            <h2 id="remove-title">Remove cook?</h2>
            <p>Remove {toRemove.name} from Recipe Box?</p>
            {removeError && (
              <p className="error" role="alert">
                {removeError}
              </p>
            )}
            <div className="actions">
              <button type="button" className="secondary" onClick={() => setToRemove(null)}>
                Cancel
              </button>
              <button type="button" className="danger" onClick={confirmRemove}>
                Remove
              </button>
            </div>
          </div>
        </div>
      )}
    </>
  );
}
