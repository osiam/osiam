# Contributing to OSIAM
Thank you for considering to contribute to OSIAM.

This document is here to offer you a few helpful notes to get you started and
make your experience as smooth as possible. They should be seen as guidelines.
They are _not_ strict rules and we won't blame anyone who tries to contribute.
By following the points in this document you will make our lifes a bit easier
but we will still be happy about your contribution if you don't.

## Reporting issues on Github
The easiest way to contribute to OSIAM is by filing issues on Github. It does
not have to be a bug, it can also be a feature request or a suggestion to
improve our documentation. We will do our best to answer your issue as soon 
as possible. Before you create an issue on Github please consider doing the
following:

* Search existing issues before raising a new one. 
* If you are reporting a bug please include as much details as possible.
  This might include:
    * Which OS you're using
    * Which version you've been running
    * Logs/Stacktrace of the error that occurred
    * Steps to reproduce
    * The expected result
    * The actual result

## The master is read only
We consider the master branch of all OSIAM projects _read only_. That means
we don't want any code to be added to it without getting reviewed by somebody
else. For that reason we don't want to accept any modifications to the master
branch that doesn't have its origin in a pull request. We believe that not 
only does this improve the quality of our code base tremendously, but also 
offers us a good way to learn from our mistakes.

## Pull Requests
If you want us to review a pull request with your modifications to OSIAM
please consider doing the following:

* Add an issue on Github and let us know that you're working on something.
* Use a feature branch, not master.
* Rebase your feature branch onto origin/master before raising the PR.
* Be descriptive in your PR and commit messages. What is it for, why is it
  needed and how you implemented it.
* Make sure the [integration tests]
  (https://github.com/osiam/connector4java-integration-tests) pass.
* Please squash related commits.

## Rebase
If while you've been working in the feature branch new commits were added to
the master branch please don't merge them but use rebase:

    git fetch origin
    git rebase origin/master

This will apply all commits on your feature branch on top of the master branch.
Any conflicts can be resolved just the same as if git merge was used. After
the conflict has been resolved use `git rebase --continue` to continue the
rebase process.

## Squash
Minor commits that only fix typos or rename variables that are related to a
bigger change should be squashed into that commit.

This can be done using `git rebase -i ( <hash> | <branch> )`

For example while working on a feature branch you'd use:

    git add .
    git commit -m "implemented feature XY"

    # push and ask for a merge/review

    git add .
    git commit --fixup $(git rev-parse HEAD)

    # push and ask for a merge/review

    git add .
    git commit --fixup $(git rev-parse HEAD)

    git rebase -i origin/master

`git commit --fixup` will mark the commit as a fixup relating to the commit
HEAD currently points to. This is useful because `git rebase -i` will then
automatically recognize the fixup commits and mark them to squash. But in
order for that to work the autosquash setting has to be enabled in the 
.gitconfig:

    git config --global rebase.autosquash true

## Good Manners
Please be civilized in all interactions concerning OSIAM that you participate
in. Most of us are working on OSIAM in our spare time while we have day jobs
and other social and familiar obligations. We don't want to deal with toxic 
or abusive behaviour while we are spending time on something we love. We want
to be a community that is welcoming to all and a place where everybody can
feel save and appreciated.

## Credits
Much of this document was taken from and inspired by the `CONTRIBUTING.md`
document of the excellent [crate](https.//crate.io) project. We use it with
a lot of love.

