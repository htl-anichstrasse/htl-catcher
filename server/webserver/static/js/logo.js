let isSpinning = true;

let illo = new Zdog.Illustration({
  element: '.zdoglogo',
  zoom: 4,
  dragRotate: true,
  // stop spinning when drag starts
  onDragStart: function() {
    isSpinning = false;
  },
});

// circle
new Zdog.Ellipse({
  addTo: illo,
  diameter: 20,
  translate: { z: 10 },
  stroke: 5,
  color: '#636',
});

// square
new Zdog.Rect({
  addTo: illo,
  width: 20,
  height: 20,
  translate: { z: -10 },
  stroke: 3,
  color: '#E62',
  fill: true,
});

function animate() {
  illo.rotate.y += isSpinning ? 0.03 : 0;
  illo.updateRenderGraph();
  requestAnimationFrame( animate );
}
animate();