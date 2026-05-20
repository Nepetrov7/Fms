import React, { useState, useEffect } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { userService } from '../../services/userService';
import { authService } from '../../services/authService';
import { UserProfileUpdateRequest, PatentDetailsForm } from '../../types';
import { PatentDetailsModal } from './PatentDetailsModal';
import { SearchableSelect } from '../common/SearchableSelect';
import { referenceService } from '../../services/referenceService';
import styles from './Profile.module.scss';

const searchCountries = (q: string): Promise<string[]> => referenceService.searchCountries(q);

const emptyPatentDetails = (): PatentDetailsForm => ({
  patentTypeId: '',
  patentNumber: '',
  patentTitle: '',
  patentIssueDate: '',
  patentExpiryDate: '',
});

export const Profile: React.FC = () => {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');
  const [patentModalOpen, setPatentModalOpen] = useState(false);
  const [patentDetails, setPatentDetails] = useState<PatentDetailsForm>(emptyPatentDetails());
  const [patentTypeName, setPatentTypeName] = useState('');
  const [patentFilled, setPatentFilled] = useState(false);
  const [formData, setFormData] = useState<UserProfileUpdateRequest>({
    firstName: '',
    lastName: '',
    middleName: '',
    citizenship: '',
    countryOfArrival: '',
    visitPurpose: '',
    arrivalDate: '',
    hasLanguageCertificate: false,
    hasPatent: false,
  });

  useEffect(() => {
    void userService
      .getProfile()
      .then((data) => {
        const hasPatent = !!data.hasPatent;
        const details: PatentDetailsForm = {
          patentTypeId: data.patentTypeId ?? '',
          patentNumber: data.patentNumber || '',
          patentTitle: data.patentTitle || '',
          patentIssueDate: data.patentIssueDate || '',
          patentExpiryDate: data.patentExpiryDate || '',
        };
        setPatentDetails(details);
        setPatentTypeName(data.patentTypeName || '');
        setPatentFilled(hasPatent && details.patentTypeId !== '' && !!details.patentNumber);
        setFormData({
          firstName: data.firstName || '',
          lastName: data.lastName || '',
          middleName: data.middleName || '',
          citizenship: data.citizenship || '',
          countryOfArrival: data.countryOfArrival || '',
          visitPurpose: data.visitPurpose || '',
          visitDurationDays: data.visitDurationDays ?? undefined,
          arrivalDate: data.arrivalDate || '',
          hasLanguageCertificate: data.hasLanguageCertificate ?? false,
          hasPatent,
          patentTypeId: details.patentTypeId === '' ? undefined : Number(details.patentTypeId),
          patentNumber: details.patentNumber,
          patentTitle: details.patentTitle,
          patentIssueDate: details.patentIssueDate || undefined,
          patentExpiryDate: details.patentExpiryDate || undefined,
        });
      })
      .catch(() => setError('Ошибка при загрузке профиля'))
      .finally(() => setLoading(false));
  }, []);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>): void => {
    const { name, value, type, checked } = e.target;
    if (name === 'hasPatent') {
      if (checked) {
        setFormData((prev) => ({ ...prev, hasPatent: true }));
        setPatentModalOpen(true);
      } else {
        setFormData((prev) => ({
          ...prev,
          hasPatent: false,
          patentTypeId: undefined,
          patentNumber: undefined,
          patentTitle: undefined,
          patentIssueDate: undefined,
          patentExpiryDate: undefined,
        }));
        setPatentDetails(emptyPatentDetails());
        setPatentTypeName('');
        setPatentFilled(false);
      }
      setError('');
      return;
    }
    setFormData((prev) => ({
      ...prev,
      [name]: type === 'checkbox' ? checked : value,
    }));
    setError('');
  };

  const handlePatentSave = (data: PatentDetailsForm, typeName: string): void => {
    setPatentDetails(data);
    setPatentTypeName(typeName);
    setPatentFilled(true);
    setFormData((prev) => ({
      ...prev,
      hasPatent: true,
      patentTypeId: data.patentTypeId === '' ? undefined : Number(data.patentTypeId),
      patentNumber: data.patentNumber,
      patentTitle: data.patentTitle || undefined,
      patentIssueDate: data.patentIssueDate || undefined,
      patentExpiryDate: data.patentExpiryDate || undefined,
    }));
    setPatentModalOpen(false);
  };

  const handleSubmit = async (e: React.FormEvent): Promise<void> => {
    e.preventDefault();
    if (formData.hasPatent && !patentFilled) {
      setError('Заполните данные патента');
      setPatentModalOpen(true);
      return;
    }
    setSaving(true);
    setError('');
    try {
      await userService.updateProfile(formData);
      navigate('/roadmap');
    } catch (err: unknown) {
      const msg = (err as { response?: { data?: { message?: string } } })?.response?.data?.message;
      setError(msg || 'Ошибка при сохранении профиля');
    } finally {
      setSaving(false);
    }
  };

  if (loading) {
    return (
      <div className={styles.profile}>
        <div className={styles.container}>Загрузка...</div>
      </div>
    );
  }

  return (
    <div className={styles.profile}>
      <div className={styles.container}>
        <h1 className={styles.title}>Профиль мигранта</h1>
        <form onSubmit={(e) => void handleSubmit(e)} className={styles.form}>
          {error && <div className={styles.error}>{error}</div>}
          <div className={styles.field}>
            <label htmlFor="lastName" className={styles.label}>Фамилия</label>
            <input id="lastName" name="lastName" className={styles.input} value={formData.lastName} onChange={handleChange} required />
          </div>
          <div className={styles.field}>
            <label htmlFor="firstName" className={styles.label}>Имя</label>
            <input id="firstName" name="firstName" className={styles.input} value={formData.firstName} onChange={handleChange} required />
          </div>
          <div className={styles.field}>
            <label htmlFor="middleName" className={styles.label}>Отчество</label>
            <input id="middleName" name="middleName" className={styles.input} value={formData.middleName || ''} onChange={handleChange} />
          </div>
          <SearchableSelect
            id="citizenship"
            label="Гражданство"
            value={formData.citizenship}
            onChange={(citizenship) => {
              setFormData((prev) => ({ ...prev, citizenship }));
              setError('');
            }}
            onSearch={searchCountries}
            required
            placeholder="Начните вводить название страны"
          />
          <SearchableSelect
            id="countryOfArrival"
            label="Страна, откуда прибыл"
            value={formData.countryOfArrival}
            onChange={(countryOfArrival) => {
              setFormData((prev) => ({ ...prev, countryOfArrival }));
              setError('');
            }}
            onSearch={searchCountries}
            required
            placeholder="Начните вводить название страны"
          />
          <div className={styles.field}>
            <label htmlFor="visitPurpose" className={styles.label}>Цель визита</label>
            <input id="visitPurpose" name="visitPurpose" className={styles.input} value={formData.visitPurpose || ''} onChange={handleChange} />
          </div>
          <div className={styles.field}>
            <label htmlFor="visitDurationDays" className={styles.label}>Срок визита (дней)</label>
            <input id="visitDurationDays" name="visitDurationDays" type="number" min={1} className={styles.input} value={formData.visitDurationDays ?? ''} onChange={handleChange} />
          </div>
          <div className={styles.field}>
            <label htmlFor="arrivalDate" className={styles.label}>Дата въезда</label>
            <input id="arrivalDate" name="arrivalDate" type="date" className={styles.input} value={formData.arrivalDate} onChange={handleChange} required />
          </div>
          <div className={styles.checkboxField}>
            <label className={styles.checkboxLabel}>
              <input type="checkbox" name="hasLanguageCertificate" checked={!!formData.hasLanguageCertificate} onChange={handleChange} />
              <span>Сертификат владения русским языком</span>
            </label>
          </div>
          <div className={styles.checkboxField}>
            <label className={styles.checkboxLabel}>
              <input type="checkbox" name="hasPatent" checked={!!formData.hasPatent} onChange={handleChange} />
              <span>Есть патент</span>
            </label>
          </div>
          {formData.hasPatent && (
            <div className={styles.patentSummary}>
              {patentFilled ? (
                <>
                  {patentTypeName && <p>Тип: {patentTypeName}</p>}
                  <p>Номер: {formData.patentNumber}</p>
                  {formData.patentTitle && <p>Описание: {formData.patentTitle}</p>}
                  {formData.patentIssueDate && <p>Выдан: {formData.patentIssueDate}</p>}
                  {formData.patentExpiryDate && <p>Действует до: {formData.patentExpiryDate}</p>}
                </>
              ) : (
                <p className={styles.patentHint}>Данные патента не заполнены</p>
              )}
              <button type="button" className={styles.linkButton} onClick={() => setPatentModalOpen(true)}>
                {patentFilled ? 'Изменить данные патента' : 'Заполнить данные патента'}
              </button>
            </div>
          )}
          <button type="submit" className={styles.button} disabled={saving}>
            {saving ? 'Сохранение...' : 'Сохранить и открыть дорожную карту'}
          </button>
        </form>
        <p className={styles.adminHint}>
          <Link to="/admin" className={styles.adminLink}>Админ-панель</Link>
          <span className={styles.hintSep}> · </span>
          <button type="button" className={styles.logoutBtn} onClick={() => { authService.logout(); navigate('/login'); }}>
            Выйти
          </button>
        </p>
      </div>
      {patentModalOpen && (
        <PatentDetailsModal
          initial={patentDetails}
          onSave={(data, typeName) => handlePatentSave(data, typeName)}
          onClose={() => {
            setPatentModalOpen(false);
            if (!patentFilled) {
              setFormData((prev) => ({ ...prev, hasPatent: false }));
            }
          }}
        />
      )}
    </div>
  );
};

