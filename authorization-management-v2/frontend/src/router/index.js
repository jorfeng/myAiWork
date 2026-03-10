import { createRouter, createWebHistory } from 'vue-router'
import AuthLetterList from '../views/AuthLetterList.vue'
import AuthLetterDetail from '../views/AuthLetterDetail.vue'
import AuthLetterEdit from '../views/AuthLetterEdit.vue'

const routes = [
  {
    path: '/',
    redirect: '/auth-letters'
  },
  {
    path: '/auth-letters',
    name: 'AuthLetterList',
    component: AuthLetterList
  },
  {
    path: '/auth-letters/create',
    name: 'AuthLetterCreate',
    component: AuthLetterEdit
  },
  {
    path: '/auth-letters/:id',
    name: 'AuthLetterDetail',
    component: AuthLetterDetail
  },
  {
    path: '/auth-letters/:id/edit',
    name: 'AuthLetterEdit',
    component: AuthLetterEdit
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router