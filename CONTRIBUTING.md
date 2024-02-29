# Contributing

Contributions should target the [`development`](https://github.com/apache/activemq-artemis-examples/tree/development) branch.

* The [`development`](https://github.com/apache/activemq-artemis-examples/tree/development) branch is used for all development toward the next Artemis release and the related Examples, whether it be improving existing examples, creating new examples, or most especially for making any updates needed to match as-yet-unreleased changes in the upcoming Artemis version.
* The [`main`](https://github.com/apache/activemq-artemis-examples/tree/main) branch contains examples for use with the latest ActiveMQ Artemis release.

Like with the main repository, most examples changes should have an [ARTEMIS Jira issue](https://issues.apache.org/jira/projects/ARTEMIS) which should be referenced in the commit log message. See [https://activemq.apache.org/issues](https://activemq.apache.org/issues) for more details.

## Testing Changes

The [GitHub Actions build](https://github.com/apache/activemq-artemis-examples/actions) for the Examples repo can also be used in your fork to test out changes. In order to enable the build in your repository, go to e.g:
`https://github.com/<your-username>/activemq-artemis-examples/actions`

Use an branch name other than _main_ to prepare your changes. Pushing commits to the examples repo on a _non-main_ branch name causes the build to check out the current revision of the [Artemis main branch](https://github.com/apache/activemq-artemis/tree/main/), build Artemis, match the examples version to the Artemis main version found, and then seperately compile (to give quick feedback when there are issues in later examples) and do verification build+run of many of the examples to check things.

If your examples updates require matching changes to the Artemis build itself, the build upon commit push will likely fail because those changes aren't yet available on the Artemis main branch. You can however also trigger the examples build _manually_ by going to e.g `https://github.com/<your-username>/activemq-artemis-examples/actions/workflows/build.yml` and clicking on the Run Workflow dropdown, which will additionally let you specify an alternative repository and branch/tag to check out and build Artemis from. This allows you to specify a related branch of your own Artemis repo fork with the necessary matching changes, enabling you to test everything together and ensure things work before raising your respective pull requests.

Pushing to the examples main branch does not check out and build Artemis since the main branch is only for use with released Artemis versions, so what is specified on the branch itself is always used as-is. The same is true for any Pull Requests raised that target the examples main branch (though again, Pull Requests should target the `development` branch).

The GitHub Actions build in the Artemis repo itself takes a somewhat similar but opposing approach, where it normally checks out the examples repo development branch and runs the respective compile check (though it does not run the examples, use the examples repo build for that).
