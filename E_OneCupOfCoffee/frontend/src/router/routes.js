/* Dynamic lazy loading */

const HelloWorld = () => import('../components/HelloWorld.vue')
const Kitchen = () => import('../components/Kitchen.vue')
const Bathroom = () => import('../components/Bathroom.vue')
const Bedroom = () => import('../components/Bedroom.vue')
const Dashboard = () => import('../components/Dashboard.vue')

export const routes = [
  {
    path: '/',
    name: 'helloworld',
    component: HelloWorld
  },
  {
    path: '/kitchen',
    name: 'kitchen',
    component: Kitchen
  },
  {
    path: '/bedroom',
    name: 'bedroom',
    component: Bedroom
  },
  {
    path: '/bathroom',
    name: 'bathroom',
    component: Bathroom
  },
  {
    path: '/dashboard',
    name: 'dashboard',
    component: Dashboard
  }
]
