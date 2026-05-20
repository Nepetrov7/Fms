import React, { useEffect, useState } from 'react';
import { patentService } from '../../services/patentService';
import type { PatentDetailsForm, PatentTypeDto } from '../../types';
import styles from './Profile.module.scss';

interface Props {
  initial: PatentDetailsForm;
  onSave: (data: PatentDetailsForm, typeName: string) => void;
  onClose: () => void;
}

export const PatentDetailsModal: React.FC<Props> = ({ initial, onSave, onClose }) => {
  const [types, setTypes] = useState<PatentTypeDto[]>([]);
  const [form, setForm] = useState<PatentDetailsForm>(initial);
  const [error, setError] = useState('');

  useEffect(() => {
    void patentService.getTypes().then(setTypes).catch(() => setError('Не удалось загрузить типы патентов'));
  }, []);

  const handleChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement>
  ): void => {
    const { name, value } = e.target;
    setForm((prev) => ({
      ...prev,
      [name]: name === 'patentTypeId' ? (value === '' ? '' : Number(value)) : value,
    }));
    setError('');
  };

  const handleSubmit = (e: React.FormEvent): void => {
    e.preventDefault();
    if (form.patentTypeId === '') {
      setError('Выберите тип патента');
      return;
    }
    if (!form.patentNumber.trim()) {
      setError('Укажите номер патента');
      return;
    }
    const typeName = types.find((t) => t.id === form.patentTypeId)?.name ?? '';
    onSave(form, typeName);
  };

  return (
    <div className={styles.modalOverlay} role="dialog" aria-modal>
      <div className={styles.modal}>
        <h2 className={styles.modalTitle}>Данные патента</h2>
        <form onSubmit={handleSubmit} className={styles.modalForm}>
          {error && <div className={styles.error}>{error}</div>}
          <div className={styles.field}>
            <label htmlFor="patentTypeId" className={styles.label}>Тип патента</label>
            <select
              id="patentTypeId"
              name="patentTypeId"
              className={styles.input}
              value={form.patentTypeId === '' ? '' : form.patentTypeId}
              onChange={handleChange}
              required
            >
              <option value="">— Выберите тип —</option>
              {types.map((t) => (
                <option key={t.id} value={t.id}>{t.name}</option>
              ))}
            </select>
          </div>
          <div className={styles.field}>
            <label htmlFor="patentNumber" className={styles.label}>Номер патента</label>
            <input
              id="patentNumber"
              name="patentNumber"
              className={styles.input}
              value={form.patentNumber}
              onChange={handleChange}
              required
            />
          </div>
          <div className={styles.field}>
            <label htmlFor="patentTitle" className={styles.label}>Описание</label>
            <textarea
              id="patentTitle"
              name="patentTitle"
              className={styles.textarea}
              value={form.patentTitle}
              onChange={handleChange}
              rows={3}
            />
          </div>
          <div className={styles.field}>
            <label htmlFor="patentIssueDate" className={styles.label}>Дата выдачи</label>
            <input
              id="patentIssueDate"
              name="patentIssueDate"
              type="date"
              className={styles.input}
              value={form.patentIssueDate}
              onChange={handleChange}
            />
          </div>
          <div className={styles.field}>
            <label htmlFor="patentExpiryDate" className={styles.label}>Дата окончания</label>
            <input
              id="patentExpiryDate"
              name="patentExpiryDate"
              type="date"
              className={styles.input}
              value={form.patentExpiryDate}
              onChange={handleChange}
            />
          </div>
          <div className={styles.modalActions}>
            <button type="button" className={styles.buttonSecondary} onClick={onClose}>Отмена</button>
            <button type="submit" className={styles.button}>Сохранить</button>
          </div>
        </form>
      </div>
    </div>
  );
};

