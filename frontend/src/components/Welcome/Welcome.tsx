import React from 'react';
import { Link } from 'react-router-dom';
import styles from './Welcome.module.scss';

export const Welcome: React.FC = () => {
  return (
    <div className={styles.welcome}>
      <div className={styles.container}>
        <h1 className={styles.title}>Добро пожаловать в ФМС</h1>
        <p className={styles.subtitle}>
          Получите персональную дорожную карту действий для оформления документов
        </p>
        <div className={styles.actions}>
          <Link to="/register" className={styles.button} tabIndex={0} aria-label="Зарегистрироваться">
            Зарегистрироваться
          </Link>
          <Link to="/login" className={`${styles.button} ${styles.buttonSecondary}`} tabIndex={0} aria-label="Войти">
            Войти
          </Link>
        </div>
      </div>
    </div>
  );
};


