import React from 'react';
import { NavLink, Outlet } from 'react-router-dom';
import styles from './AdminLayout.module.scss';

const nav = [
  { to: '/admin/tasks', label: 'Задачи дорожной карты' },
];

export const AdminLayout: React.FC = () => {
  return (
    <div className={styles.layout}>
      <aside className={styles.sidebar}>
        <div className={styles.brand}>
          <span className={styles.brandTitle}>FMS</span>
          <span className={styles.brandSub}>Админ-панель</span>
        </div>
        <nav className={styles.nav}>
          {nav.map((item) => (
            <NavLink
              key={item.to}
              to={item.to}
              className={({ isActive }) =>
                `${styles.navLink} ${isActive ? styles.navLinkActive : ''}`
              }
            >
              {item.label}
            </NavLink>
          ))}
        </nav>
        <NavLink to="/" className={styles.backLink}>
          ← На сайт
        </NavLink>
      </aside>
      <main className={styles.main}>
        <Outlet />
      </main>
    </div>
  );
};
