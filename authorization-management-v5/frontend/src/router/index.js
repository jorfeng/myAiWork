import Vue from 'vue';
import VueRouter from 'vue-router';
import AuthLetterList from '../views/AuthLetterList.vue';
import AuthLetterDetail from '../views/AuthLetterDetail.vue';
import RuleParamConfig from '../views/RuleParamConfig.vue';

Vue.use(VueRouter);

const routes = [
  {
    path: '/AuthLetterList',
    name: 'AuthLetterList',
    component: AuthLetterList
  },
  {
    path: '/AuthLetterDetail',
    name: 'AuthLetterDetail',
    component: AuthLetterDetail
  },
  {
    path: '/RuleParamConfig',
    name: 'RuleParamConfig',
    component: RuleParamConfig
  },
  {
    path: '/',
    redirect: '/AuthLetterList'
  }
];

const router = new VueRouter({
  mode: 'hash',
  routes
});

export default router;