/*
 * GenerateTerrain.cpp
 *
 *  Created on: Apr 14, 2014
 *      Author: orange
 */

#include <iostream>
#include "noiseutils.h"



using namespace noise;

int main (int argc, char** argv)
{
	module::Perlin myModule;

	utils::NoiseMap heightMap;
	utils::NoiseMapBuilderPlane heightMapBuilder;
	heightMapBuilder.SetSourceModule (myModule);
	heightMapBuilder.SetDestNoiseMap (heightMap);
	heightMapBuilder.SetDestSize (256, 256);
	heightMapBuilder.SetBounds (2.0, 6.0, 1.0, 5.0);
	heightMapBuilder.Build ();

	utils::RendererImage renderer;
	utils::Image image;
	renderer.SetSourceNoiseMap (heightMap);
	renderer.SetDestImage (image);
	renderer.Render ();



  return 0;
}
