import React, { useCallback, useEffect, useState } from 'react';
import { adminService } from '../../services/adminService';
import type {
  DisplayRuleGroupDto,
  DisplayRuleConditionRequest,
  OperatorMeta,
  ParameterMeta,
} from '../../types/admin';
import styles from './AdminPages.module.scss';

interface Props {
  taskId: number;
  taskTitle: string;
  onClose: () => void;
}

const emptyCondition = (): DisplayRuleConditionRequest => ({
  parameterKey: 'ALL',
  operator: 'EQ',
  value: 'true',
});

export const TaskRulesModal: React.FC<Props> = ({ taskId, taskTitle, onClose }) => {
  const [groups, setGroups] = useState<DisplayRuleGroupDto[]>([]);
  const [parameters, setParameters] = useState<ParameterMeta[]>([]);
  const [operators, setOperators] = useState<OperatorMeta[]>([]);
  const [loading, setLoading] = useState(true);
  const [err, setErr] = useState('');
  const [editingGroupId, setEditingGroupId] = useState<number | null>(null);
  const [conditions, setConditions] = useState<DisplayRuleConditionRequest[]>([emptyCondition()]);

  const load = useCallback(async () => {
    setErr('');
    setLoading(true);
    try {
      const [g, p, o] = await Promise.all([
        adminService.getDisplayRules(taskId),
        adminService.getParameters(),
        adminService.getOperators(),
      ]);
      setGroups(g);
      setParameters(p);
      setOperators(o);
    } catch {
      setErr('Не удалось загрузить правила');
    } finally {
      setLoading(false);
    }
  }, [taskId]);

  useEffect(() => {
    void load();
  }, [load]);

  const resetForm = (): void => {
    setEditingGroupId(null);
    setConditions([emptyCondition()]);
  };

  const saveGroup = async (): Promise<void> => {
    setErr('');
    const body = { conditions: conditions.filter((c) => c.parameterKey) };
    if (body.conditions.length === 0) {
      setErr('Добавьте хотя бы одно условие');
      return;
    }
    try {
      if (editingGroupId != null) {
        await adminService.updateDisplayRuleGroup(taskId, editingGroupId, body);
      } else {
        await adminService.createDisplayRuleGroup(taskId, body);
      }
      resetForm();
      await load();
    } catch {
      setErr('Ошибка сохранения');
    }
  };

  const editGroup = (group: DisplayRuleGroupDto): void => {
    setEditingGroupId(group.ruleGroupId);
    setConditions(
      group.conditions.map((c) => ({
        parameterKey: c.parameterKey,
        operator: c.operator,
        value: c.value,
      }))
    );
  };

  const deleteGroup = async (groupId: number): Promise<void> => {
    if (!window.confirm('Удалить группу правил?')) return;
    try {
      await adminService.deleteDisplayRuleGroup(taskId, groupId);
      if (editingGroupId === groupId) resetForm();
      await load();
    } catch {
      setErr('Не удалось удалить');
    }
  };

  const formatCondition = (c: DisplayRuleConditionRequest): string => {
    const p = parameters.find((x) => x.value === c.parameterKey)?.label ?? c.parameterKey;
    const o = operators.find((x) => x.value === c.operator)?.label ?? c.operator;
    if (c.parameterKey === 'ALL') return 'Для всех';
    return `${p} ${o} ${c.value ?? ''}`;
  };

  return (
    <div className={styles.modalOverlay} role="dialog" aria-modal>
      <div className={`${styles.modal} ${styles.modalWide}`}>
        <div className={styles.modalHeader}>Правила отображения: «{taskTitle}»</div>
        <div className={styles.modalBody}>
          {err && <div className={styles.error}>{err}</div>}
          {loading ? (
            <p>Загрузка…</p>
          ) : (
            <>
              <p className={styles.rulesIntro}>
                Группы объединяются через <strong>ИЛИ</strong>: достаточно одной подходящей группы.
                Внутри группы условия — через <strong>И</strong>. Без правил пункт не показывается никому.
                {' '}
                Если нужны несколько условий сразу (например, «не Беларусь» <strong>и</strong> «до 90 дней»),
                добавьте их в <strong>одну</strong> группу, а не в разные.
              </p>
              <div className={styles.tableWrap}>
                <table className={styles.table}>
                  <thead>
                    <tr><th>Группа</th><th>Условия</th><th /></tr>
                  </thead>
                  <tbody>
                    {groups.length === 0 ? (
                      <tr><td colSpan={3}>Правил нет — пункт скрыт для всех</td></tr>
                    ) : (
                      groups.map((g) => (
                        <tr key={g.ruleGroupId}>
                          <td>#{g.ruleGroupId}</td>
                          <td>{g.conditions.map((c) => formatCondition(c)).join(' И ')}</td>
                          <td>
                            <button type="button" className={styles.smallBtn} onClick={() => editGroup(g)}>Редактировать</button>
                            {' '}
                            <button type="button" className={styles.smallBtnDanger} onClick={() => void deleteGroup(g.ruleGroupId)}>Удалить</button>
                          </td>
                        </tr>
                      ))
                    )}
                  </tbody>
                </table>
              </div>

              <h3 className={styles.subSectionTitle}>
                {editingGroupId != null ? `Редактировать группу #${editingGroupId}` : 'Новая группа правил'}
              </h3>
              {conditions.map((cond, idx) => (
                <div key={idx} className={styles.inlineForm}>
                  <div className={styles.field}>
                    <label>Параметр</label>
                    <select
                      value={cond.parameterKey}
                      onChange={(e) => {
                        const v = e.target.value;
                        setConditions((prev) => prev.map((c, i) => (i === idx ? { ...c, parameterKey: v } : c)));
                      }}
                    >
                      {parameters.map((p) => (
                        <option key={p.value} value={p.value}>{p.label}</option>
                      ))}
                    </select>
                  </div>
                  {cond.parameterKey !== 'ALL' && (
                    <>
                      <div className={styles.field}>
                        <label>Оператор</label>
                        <select
                          value={cond.operator}
                          onChange={(e) => setConditions((prev) => prev.map((c, i) => (i === idx ? { ...c, operator: e.target.value } : c)))}
                        >
                          {operators.map((o) => (
                            <option key={o.value} value={o.value}>{o.label}</option>
                          ))}
                        </select>
                      </div>
                      <div className={styles.field}>
                        <label>Значение</label>
                        <input
                          value={cond.value ?? ''}
                          onChange={(e) => setConditions((prev) => prev.map((c, i) => (i === idx ? { ...c, value: e.target.value } : c)))}
                          placeholder={cond.parameterKey.includes('HAS_') ? 'true / false' : 'например: Беларусь или 30'}
                        />
                      </div>
                    </>
                  )}
                  {conditions.length > 1 && (
                    <button type="button" className={styles.smallBtnDanger} onClick={() => setConditions((prev) => prev.filter((_, i) => i !== idx))}>
                      Убрать условие
                    </button>
                  )}
                </div>
              ))}
              <div className={styles.toolbar}>
                <button type="button" className={styles.btnGhost} onClick={() => setConditions((prev) => [...prev, emptyCondition()])}>
                  + Условие в группе
                </button>
                <button type="button" className={styles.btnPrimary} onClick={() => void saveGroup()}>
                  {editingGroupId != null ? 'Сохранить группу' : 'Добавить группу'}
                </button>
                {editingGroupId != null && (
                  <button type="button" className={styles.btnGhost} onClick={resetForm}>Отмена</button>
                )}
              </div>
            </>
          )}
        </div>
        <div className={styles.modalFooter}>
          <button type="button" className={styles.btnGhost} onClick={onClose}>Закрыть</button>
        </div>
      </div>
    </div>
  );
};

