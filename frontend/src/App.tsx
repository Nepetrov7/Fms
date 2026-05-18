import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { Profile } from './components/Profile/Profile';
import { Roadmap } from './components/Roadmap/Roadmap';
import { Login } from './components/Auth/Login';
import { Register } from './components/Auth/Register';
import { ProtectedRoute } from './components/Auth/ProtectedRoute';
import { AdminLayout } from './components/Admin/AdminLayout';
import { AdminTasks } from './components/Admin/AdminTasks';
import './App.scss';

const App: React.FC = () => {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route path="/admin" element={<AdminLayout />}>
          <Route index element={<Navigate to="tasks" replace />} />
          <Route path="tasks" element={<AdminTasks />} />
        </Route>
        <Route element={<ProtectedRoute />}>
          <Route path="/" element={<Profile />} />
          <Route path="/roadmap" element={<Roadmap />} />
        </Route>
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </BrowserRouter>
  );
};

export default App;


