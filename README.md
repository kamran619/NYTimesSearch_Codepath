App: A news reader app which lets users search the New York times article database and read articles. 

## Features
- [x] User can enter a search query that will display a grid of news articles using the thumbnail and headline from the New York Times Search API. (3 points)
- [x] User can click on "settings" which allows selection of advanced search options to filter results. (3 points)
- [x] User can configure advanced search filters such as: (points included above)
    Begin Date (using a date picker)
    News desk values (Arts, Fashion & Style, Sports)
    Sort order (oldest or newest)
- [x] Subsequent searches will have any filters applied to the search results. (1 point)
- [x] User can tap on any article in results to view the contents in an embedded browser. (2 points)
- [x] User can scroll down "infinitely" to continue loading more news articles. The maximum number of articles is limited by the API search. (1 point)

## Extra
- [x] Robust error handling, check if internet is available, handle error cases, network failures. (1 point)
- [x] Use the ActionBar SearchView or custom layout as the query box instead of an EditText. (1 point)
- [ ] User can share a link to their friends or email it to themselves. (1 point)
- [x] Replace Filter Settings Activity with a lightweight modal overlay. (2 points)
- [ ] Improve the user interface and experiment with image assets and/or styling and coloring (1 to 3 points depending on the difficulty of UI improvements)
- [x] Use the RecyclerView with the StaggeredGridLayoutManager to display improve the grid of image results (see Picasso guide too). (2 points)
- [ ] For different news articles that only have text or have text with thumbnails, use Heterogenous Layouts with RecyclerView. (2 points)
- [x] Apply the popular ButterKnife annotation library to reduce view boilerplate. (1 point)
- [x] Use Parcelable instead of Serializable using the popular Parceler library. (1 point)
- [x] Leverage the popular GSON library to streamline the parsing of JSON data. (1 point)
- [x] Replace Picasso with Glide for more efficient image rendering. (1 point)

## Demo
![Alt text](/demo/nytimes_demo.gif)


### Open Source Libraries Used
- [ButterKnife](http://jakewharton.github.io/butterknife/) Annotation library to reduce view boilerplate
- [Glide](https://github.com/bumptech/glide) Image downloading and caching library 
- [OKHttp](http://square.github.io/okhttp/) Networking
- [Parceler](https://github.com/johncarl81/parceler) Android Parcelables made easy through code generation
