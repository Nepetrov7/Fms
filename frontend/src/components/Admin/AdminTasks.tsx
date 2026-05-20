import React, { useCallback, useEffect, useState } from 'react';
import { adminService } from '../../services/adminService';
import type { AdminTaskDto } from '../../types/admin';
import { TaskRulesModal } from './TaskRulesModal';
import styles from './AdminPages.module.scss';

export const AdminTasks: React.FC = () => {
  const [tasks, setTasks] = useState<AdminTaskDto[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [editing, setEditing] = useState<AdminTaskDto | null>(null);
  const [creating, setCreating] = useState(false);
  const [rulesTask, setRulesTask] = useState<{ id: number; title: string } | null>(null);
  const [groupNames, setGroupNames] = useState<string[]>([]);
  const [form, setForm] = useState({
    title: '',
    groupName: '',
    description: '',
    daysToComplete: '' as number | '',
    active: true,
  });
  const [saving, setSaving] = useState(false);

  const formOpen = creating || editing !== null;

  const refresh = useCallback(async () => {
    setError('');
    try {
      setTasks(await adminService.getTasks());
    } catch {
      setError('Не удалось загрузить задачи');
    } finally {
      setLoading(false);
    }
  }, []);

  const loadGroupNames = useCallback(async () => {
    try {
      setGroupNames(await adminService.getGroupNames());
    } catch {
      setGroupNames([]);
    }
  }, []);

  useEffect(() => {
    void refresh();
    void loadGroupNames();
  }, [refresh, loadGroupNames]);

  useEffect(() => {
    if (formOpen) {
      void loadGroupNames();
    }
  }, [formOpen, loadGroupNames]);

  const openCreate = (): void => {
    setError('');
    setCreating(true);
    setEditing(null);
    setForm({ title: '', groupName: '', description: '', daysToComplete: '', active: true });
  };

  const openEdit = (task: AdminTaskDto): void => {
    setError('');
    setEditing(task);
    setCreating(false);
    setForm({
      title: task.title,
      groupName: task.groupName || '',
      description: task.description || '',
      daysToComplete: task.daysToComplete ?? '',
      active: task.active,
    });
  };

  const closeForm = (): void => {
    setCreating(false);
    setEditing(null);
  };

  const submit = async (): Promise<void> => {
    if (!form.title.trim()) {
      setError('Укажите название');
      return;
    }
    if (!form.groupName.trim()) {
      setError('Укажите название группы на карте');
      return;
    }
    setSaving(true);
    setError('');
    try {
      const payload = {
        title: form.title.trim(),
        groupName: form.groupName.trim(),
        description: form.description.trim() || null,
        daysToComplete: form.daysToComplete === '' ? null : Number(form.daysToComplete),
      };
      if (creating) {
        await adminService.createTask(payload);
      } else if (editing) {
        await adminService.updateTask(editing.id, { ...payload, active: form.active });
      }
      closeForm();
      await refresh();
    } catch (err: unknown) {
      const msg = (err as { response?: { data?: { message?: string } } })?.response?.data?.message;
      setError(msg || 'Ошибка сохранения');
    } finally {
      setSaving(false);
    }
  };

  const handleDelete = async (task: AdminTaskDto): Promise<void> => {
    if (!window.confirm(`Удалить «${task.title}»?`)) return;
    try {
      await adminService.deleteTask(task.id);
      await refresh();
    } catch {
      setError('Не удалось удалить');
    }
  };

  if (loading) {
    return (
      <div className={styles.page}>
        <p>Загрузка…</p>
      </div>
    );
  }

  return (
    <div className={styles.page}>
      <h1 className={styles.h1}>Пункты дорожной карты</h1>
      <p className={styles.lead}>
        Общий список шагов для всех мигрантов. Для каждого пункта задайте правила, кому он показывается.
      </p>
      {error && !formOpen && <div className={styles.error}>{error}</div>}
      <div className={styles.toolbar}>
        <button type="button" className={styles.btnPrimary} onClick={openCreate}>
          + Добавить пункт
        </button>
      </div>

      <div className={styles.tableWrap}>
        <table className={styles.table}>
          <thead>
            <tr>
              <th>Название</th>
              <th>Группа на карте</th>
              <th>Дней с въезда</th>
              <th>Активен</th>
              <th />
            </tr>
          </thead>
          <tbody>
            {tasks.length === 0 ? (
              <tr><td colSpan={5}>Пунктов пока нет</td></tr>
            ) : (
              tasks.map((task) => (
                <tr key={task.id}>
                  <td>{task.title}</td>
                  <td>{task.groupName || '—'}</td>
                  <td>{task.daysToComplete ?? '—'}</td>
                  <td>{task.active ? 'да' : 'нет'}</td>
                  <td>
                    <button
                      type="button"
                      className={styles.smallBtn}
                      onClick={() => setRulesTask({ id: task.id, title: task.title })}
                    >
                      Правила показа
                    </button>
                    {' '}
                    <button type="button" className={styles.smallBtn} onClick={() => openEdit(task)}>
                      Редактировать
                    </button>
                    {' '}
                    <button type="button" className={styles.smallBtnDanger} onClick={() => void handleDelete(task)}>
                      Удалить
                    </button>
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>

      {formOpen && (
        <div
          className={styles.modalOverlay}
          role="dialog"
          aria-modal
          aria-labelledby="task-form-title"
          onClick={closeForm}
        >
          <div className={styles.modal} onClick={(e) => e.stopPropagation()}>
            <div id="task-form-title" className={styles.modalHeader}>
              {creating ? 'Новый пункт дорожной карты' : 'Редактирование пункта'}
            </div>
            <div className={styles.modalBody}>
              {error && <div className={styles.error}>{error}</div>}
              <div className={styles.formGrid}>
                <div className={styles.field}>
                  <label className={styles.label} htmlFor="task-title">Название</label>
                  <input
                    id="task-title"
                    className={styles.input}
                    value={form.title}
                    onChange={(e) => setForm((f) => ({ ...f, title: e.target.value }))}
                    autoFocus
                  />
                </div>
                <div className={styles.field}>
                  <label className={styles.label} htmlFor="task-group">Название группы на карте</label>
                  <input
                    id="task-group"
                    className={styles.input}
                    list="task-group-options"
                    value={form.groupName}
                    onChange={(e) => setForm((f) => ({ ...f, groupName: e.target.value }))}
                    placeholder="Выберите из списка или введите новое"
                  />
                  <datalist id="task-group-options">
                    {groupNames.map((name) => (
                      <option key={name} value={name} />
                    ))}
                  </datalist>
                  <p className={styles.fieldHint}>
                    Пункты с одинаковым названием группы отображаются в одном блоке дорожной карты.
                  </p>
                </div>
                <div className={styles.field}>
                  <label className={styles.label} htmlFor="task-desc">Описание</label>
                  <textarea
                    id="task-desc"
                    className={styles.textarea}
                    value={form.description}
                    onChange={(e) => setForm((f) => ({ ...f, description: e.target.value }))}
                  />
                </div>
                <div className={styles.field}>
                  <label className={styles.label} htmlFor="task-days">Срок (дней с въезда)</label>
                  <input
                    id="task-days"
                    type="number"
                    min={0}
                    className={styles.input}
                    value={form.daysToComplete === '' ? '' : form.daysToComplete}
                    onChange={(e) =>
                      setForm((f) => ({
                        ...f,
                        daysToComplete: e.target.value === '' ? '' : Number(e.target.value),
                      }))
                    }
                  />
                </div>
                {editing && (
                  <div className={styles.checkboxRow}>
                    <input
                      id="task-active"
                      type="checkbox"
                      checked={form.active}
                      onChange={(e) => setForm((f) => ({ ...f, active: e.target.checked }))}
                    />
                    <label className={styles.label} htmlFor="task-active">
                      Пункт активен (показывается на карте)
                    </label>
                  </div>
                )}
              </div>
            </div>
            <div className={styles.modalFooter}>
              <button type="button" className={styles.btnGhost} onClick={closeForm} disabled={saving}>
                Отмена
              </button>
              <button type="button" className={styles.btnPrimary} disabled={saving} onClick={() => void submit()}>
                {saving ? 'Сохранение…' : 'Сохранить'}
              </button>
            </div>
          </div>
        </div>
      )}

      {rulesTask && (
        <TaskRulesModal taskId={rulesTask.id} taskTitle={rulesTask.title} onClose={() => setRulesTask(null)} />
      )}
    </div>
  );
};
