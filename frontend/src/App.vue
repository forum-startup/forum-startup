<script setup>
import NavBar from "./components/NavBar.vue";
import Footer from "./components/Footer.vue";
</script>

<template>
  <div class="min-h-screen flex flex-col">
    <NavBar />

    <main class="flex-1 flex flex-col bg-gray-900">
      <router-view />
    </main>

    <Footer />
  </div>
</template>
