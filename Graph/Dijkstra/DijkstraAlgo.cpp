#include "DijkstraAlgo.h"
namespace Graph {

DijkstraAlgo::DijkstraAlgo()
{

}

DijkstraAlgo::~DijkstraAlgo()
{
}

// verify and modify
void DijkstraAlgo::find_shortestPath()
{
	/*
	std::set<unlong>                              searchedList;
	std::priority_queue<std::pair<ulong, ulong>>  frontierList;

	// frontier list contains pairs of (cost, city)
	// std::pair is sorted by first then second so the frontierList
	// is automatically sorted by the cost of getting to a city.
	// Add the start city as the only city in the frontier list.
	frontierList.push(std::make_pair(0, start));

	while (!frontierList.empty())
	{
		std::pair<ulong, ulong> next = frontierList.top();
		frontierList.pop();

		if (next.second == end)
		{
			std::cout << "Min Cost: " << next.first << "\n";
			return;
		}
		if (searchedList.find(next.second) != searchedList.end())
		{
			continue; // We already found this node. move on.
			// Note: continue starts the next loop iteration.
		}
		// We have found the lowest cost to this city.
		// So mark that information by placing it in the searched list.
		searchedList.insert(next.second);

		// Add all city's that can be reached from here to the frontier list.
		for (std::map<ulong, unlong>::const_iterator loop = graph[next.second].begin(); loop != graph[next.second].end(); ++loop)
		{
			// Add item for each city (cost = cost to get here + cost of road)
			frontierList.push(std::make_pair(next.first + loop->second, loop->first));
		}
	}
	*/
}

} /*end of Graph namespace*/

