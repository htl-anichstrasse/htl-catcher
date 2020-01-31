let sceneSize = 50;
let isSpinning = true;

let illo = new Zdog.Illustration({
  element: ".zdoglogo",
  zoom: 0.35,
  dragRotate: true,
  // stop spinning when drag starts
  onDragStart: function() {
    isSpinning = false;
  },
  onResize: function(width, height) {
    this.zoom = Math.floor(Math.min(width, height) / sceneSize);
  }
});

// Green Box
new Zdog.Box({
  addTo: illo,
  width: 100,
  height: 40,
  depth: 40,
  stroke: 1,
  fill: true,
  leftFace: "#29b284",
  rightFace: "#29b284",
  topFace: "#29b284",
  bottomFace: "#29b284",
  frontFace: "#1c7e5f",
  rearFace: "#1c7e5f",
  translate: { z: -55, x: -20 }
});

// Blue Box
new Zdog.Box({
  addTo: illo,
  width: 100,
  height: 40,
  depth: 40,
  stroke: 1,
  fill: true,
  leftFace: "yellow",
  rightFace: "#0052a3",
  topFace: "#0052a3",
  bottomFace: "#0052a3",
  frontFace: "#00274e",
  rearFace: "#00274e",
  translate: { z: +45, x: +20 }
});

// Yellow Box
new Zdog.Box({
  addTo: illo,
  width: 40,
  height: 40,
  depth: 100,
  fill: true,
  stroke: 1,
  topFace: "#ffe407",
  bottomFace: "#ffe407",
  rightFace: "#debc3d",
  leftFace: "#debc3d",
  frontFace: "#ffe407",
  rearFace: "#ffe407",
  translate: { x: +50, z: -25 }
});

// Red Box
new Zdog.Box({
  addTo: illo,
  stroke: 1,
  height: 40,
  width: 40,
  depth: 100,
  fill: true,
  topFace: "#d50404",
  bottomFace: "#d50404",
  leftFace: "#840001",
  rightFace: "#840001",
  rearFace: "#d50404",
  frontFace: "#d50404",
  translate: { x: -50, z: +15 }
});

// Alignment of the logo
illo.rotate.x += 2;
illo.rotate.y += 4;
illo.rotate.z += 2.9;
illo.updateRenderGraph();

// continous animation of logo
function animate() {
  illo.rotate.y += 0.01;
  illo.rotate.x += 0.01;
  illo.rotate.z += 0.01;
  illo.updateRenderGraph();
  requestAnimationFrame(animate);
}
animate();
