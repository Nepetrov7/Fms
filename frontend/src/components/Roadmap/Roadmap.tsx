import React, { useState, useEffect, useMemo } from 'react';
import { useNavigate } from 'react-router-dom';
import { roadmapService } from '../../services/roadmapService';
import { authService } from '../../services/authService';
import { RoadmapResponse, RoadmapTaskDto } from '../../types';
import styles from './Roadmap.module.scss';

function groupByName(tasks: RoadmapTaskDto[]): { key: string; label: string; tasks: RoadmapTaskDto[] }[] {
  const order: string[] = [];
  const map = new Map<string, RoadmapTaskDto[]>();
  for (const task of tasks) {
    const key = task.groupName?.trim() || '';
    if (!map.has(key)) {
      map.set(key, []);
      order.push(key);
    }
    map.get(key)!.push(task);
  }
  return order.map((key) => ({
    key,
    label: key || 'Без группы',
    tasks: map.get(key)!,
  }));
}

export const Roadmap: React.FC = () => {
  const navigate = useNavigate();
  const [roadmap, setRoadmap] = useState<RoadmapResponse | null>(null);
  const [completedIds, setCompletedIds] = useState<Set<number>>(new Set());
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [updating, setUpdating] = useState(false);

  const loadRoadmap = async (): Promise<void> => {
    const data = await roadmapService.getRoadmap();
    setRoadmap(data);
    const ids = data.completedTaskIds;
    setCompletedIds(new Set(Array.isArray(ids) ? ids : []));
  };

  useEffect(() => {
    void loadRoadmap()
      .catch(() => setError('Ошибка при загрузке дорожной карты'))
      .finally(() => setLoading(false));
  }, []);

  const sections = useMemo(
    () => (roadmap?.tasks ? groupByName(roadmap.tasks) : []),
    [roadmap?.tasks]
  );

  const progress = useMemo(() => {
    if (!roadmap?.tasks.length) return { done: 0, total: 0, percent: 0 };
    const total = roadmap.tasks.length;
    const done = roadmap.tasks.filter((t) => completedIds.has(t.id)).length;
    return { done, total, percent: Math.round((done / total) * 100) };
  }, [roadmap?.tasks, completedIds]);

  const handleToggle = async (taskId: number): Promise<void> => {
    setUpdating(true);
    try {
      if (completedIds.has(taskId)) {
        await roadmapService.uncompleteTask(taskId);
      } else {
        await roadmapService.completeTask(taskId);
      }
      await loadRoadmap();
    } finally {
      setUpdating(false);
    }
  };

  if (loading) {
    return (
      <div className={styles.roadmap}>
        <div className={styles.container}>
          <p className={styles.loadingText}>Загрузка дорожной карты…</p>
        </div>
      </div>
    );
  }

  if (error || !roadmap) {
    return (
      <div className={styles.roadmap}>
        <div className={styles.container}>
          <div className={styles.error}>
            <p>{error || 'Нет данных'}</p>
            <div className={styles.errorActions}>
              <button type="button" className={styles.button} onClick={() => navigate('/')}>
                На главную
              </button>
            </div>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className={styles.roadmap}>
      <header className={styles.header}>
        <div className={`${styles.container} ${styles.headerInner}`}>
          <div>
            <h1 className={styles.title}>Дорожная карта</h1>
            <p className={styles.subtitle}>Ваши шаги по легализации пребывания и работы</p>
          </div>
          <div className={styles.actions}>
            <button type="button" className={styles.buttonSecondary} onClick={() => navigate('/')}>
              Профиль
            </button>
            <button
              type="button"
              className={styles.buttonSecondary}
              onClick={() => {
                authService.logout();
                navigate('/login');
              }}
            >
              Выйти
            </button>
          </div>
        </div>
      </header>

      <div className={styles.container}>
        {!roadmap.isProfileComplete ? (
          <div className={styles.message}>
            <p>{roadmap.message}</p>
            <button type="button" className={styles.button} onClick={() => navigate('/')}>
              Заполнить профиль
            </button>
          </div>
        ) : (
          <>
            <div className={styles.message}>
              <p>{roadmap.message}</p>
              {progress.total > 0 && (
                <div className={styles.progressWrap}>
                  <div className={styles.progressBar}>
                    <div
                      className={styles.progressFill}
                      style={{ width: `${progress.percent}%` }}
                    />
                  </div>
                  <span className={styles.progressLabel}>
                    Выполнено {progress.done} из {progress.total} ({progress.percent}%)
                  </span>
                </div>
              )}
            </div>

            {roadmap.tasks.length === 0 ? (
              <div className={styles.success}>
                <h2>Нет активных шагов</h2>
                <p>
                  Все подходящие пункты выполнены или для вашего профиля пока не настроены правила
                  отображения.
                </p>
              </div>
            ) : (
              <div className={styles.roadmapTrack}>
                <div className={styles.startPoint}>
                  <span className={styles.startIcon} aria-hidden>🛬</span>
                  <span className={styles.startLabel}>Начало пути</span>
                </div>

                <div className={styles.chapters}>
                  {sections.map((section) => {
                    const sectionDone = section.tasks.every((t) => completedIds.has(t.id));
                    return (
                      <section
                        key={section.key || '_default'}
                        className={`${styles.chapterWrapper} ${sectionDone ? styles.chapterWrapperDone : ''}`}
                      >
                        <article className={`${styles.chapter} ${sectionDone ? styles.chapterCompleted : ''}`}>
                          <h2 className={styles.chapterTitle}>
                            {section.label}
                            {sectionDone && (
                              <span className={styles.completedBadge}>Раздел выполнен</span>
                            )}
                          </h2>
                          <ol className={styles.itemsList}>
                            {section.tasks.map((task) => {
                              const isDone = completedIds.has(task.id);
                              return (
                                <li
                                  key={task.id}
                                  className={`${styles.item} ${isDone ? styles.itemCompleted : ''}`}
                                >
                                  <label className={styles.itemLabel}>
                                    <input
                                      type="checkbox"
                                      className={styles.checkbox}
                                      checked={isDone}
                                      disabled={updating}
                                      onChange={() => void handleToggle(task.id)}
                                    />
                                    <span className={styles.itemText}>{task.title}</span>
                                  </label>
                                  {task.description && (
                                    <p className={styles.itemDescription}>{task.description}</p>
                                  )}
                                  {task.daysToComplete != null && (
                                    <p className={styles.deadline}>
                                      Рекомендуемый срок: до {task.daysToComplete} дн. с даты въезда
                                    </p>
                                  )}
                                </li>
                              );
                            })}
                          </ol>
                        </article>
                      </section>
                    );
                  })}
                </div>

                <div className={styles.finishPoint}>
                  <span className={styles.finishIcon} aria-hidden>✅</span>
                  <span className={styles.finishLabel}>Цель</span>
                </div>
              </div>
            )}
          </>
        )}
      </div>
    </div>
  );
};
