import React, { useCallback, useEffect, useId, useRef, useState } from 'react';
import styles from './SearchableSelect.module.scss';

interface Props {
  id?: string;
  label: string;
  value: string;
  onChange: (value: string) => void;
  onSearch: (query: string) => Promise<string[]>;
  required?: boolean;
  placeholder?: string;
}

/**
 * Поле с автодополнением: поиск по первым буквам и выбор из списка.
 */
export const SearchableSelect: React.FC<Props> = ({
  id: idProp,
  label,
  value,
  onChange,
  onSearch,
  required,
  placeholder,
}) => {
  const autoId = useId();
  const inputId = idProp ?? autoId;
  const listId = `${inputId}-list`;
  const wrapRef = useRef<HTMLDivElement>(null);

  const [open, setOpen] = useState(false);
  const [options, setOptions] = useState<string[]>([]);
  const [loading, setLoading] = useState(false);

  const loadOptions = useCallback(
    async (query: string) => {
      setLoading(true);
      try {
        setOptions(await onSearch(query));
      } catch {
        setOptions([]);
      } finally {
        setLoading(false);
      }
    },
    [onSearch]
  );

  useEffect(() => {
    if (!open) return;
    const t = window.setTimeout(() => void loadOptions(value), 200);
    return () => window.clearTimeout(t);
  }, [value, open, loadOptions]);

  useEffect(() => {
    const onDocClick = (e: MouseEvent): void => {
      if (wrapRef.current && !wrapRef.current.contains(e.target as Node)) {
        setOpen(false);
      }
    };
    document.addEventListener('mousedown', onDocClick);
    return () => document.removeEventListener('mousedown', onDocClick);
  }, []);

  const pick = (item: string): void => {
    onChange(item);
    setOpen(false);
  };

  return (
    <div className={styles.wrap} ref={wrapRef}>
      <label className={styles.label} htmlFor={inputId}>
        {label}
      </label>
      <input
        id={inputId}
        className={styles.input}
        list={listId}
        value={value}
        required={required}
        placeholder={placeholder}
        autoComplete="off"
        onFocus={() => {
          setOpen(true);
          void loadOptions(value);
        }}
        onChange={(e) => {
          onChange(e.target.value);
          setOpen(true);
        }}
      />
      <datalist id={listId}>
        {options.map((o) => (
          <option key={o} value={o} />
        ))}
      </datalist>
      {open && options.length > 0 && (
        <ul className={styles.dropdown} role="listbox">
          {loading && <li className={styles.hint}>Загрузка…</li>}
          {!loading &&
            options.map((o) => (
              <li key={o}>
                <button type="button" className={styles.option} onMouseDown={() => pick(o)}>
                  {o}
                </button>
              </li>
            ))}
        </ul>
      )}
    </div>
  );
};
