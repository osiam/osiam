# Introduction

The audience of this document are members of the OSIAM Team.

The purpose of this document is to describe the process to continuously increase the quality of
OSIAM from the perspective of security.

This document describes the SDL (Security Development Lifecycle) which will fit into the short iterations of an agile development. It is splitted in three categories defined by frequency of completion.

# Every iteration practices

The first category consists of the SDL requirements that are essential to security (see http://www.blackhat.com/presentations/bh-dc-10/Sullivan_Bryan/BlackHat-DC-2010-Sullivan-SDL-Agile-wp.pdf and 
http://www.microsoft.com/security/sdl/discover/sdlagile.aspx)

* Run static code analysis tools per build
* Define a list of approved tools and associated security checks
* Threat model all new features, determine risks from those threats, and establish appropriate countermeasures 
* Secure coding: cross-site scripting, interpreter injections (SQL, LDAP, JSON injections and so on), use only strong crypto, use filtering and escaping
* Secure design: attack surface reduction, principle of least privilege (a user should have only as many rights as it needs to do its job), secure defaults	
* Security review: Includes an examination of threat models, tools outputs, and performance against the quality gates and bug bars (a bug bar is a list of security vulnerabilities with then according severity: for example there can be a constraint that there will be no release if there are known vulnerabilities in the application with a "critical" or "important" rating). It results in one of three different outcomes: Passed, Passed with exceptions, failed  

# Bucket Requirements
The second category of SDL requirements consists of tasks that must be performed on a regular basis over the lifetime of the project but that are not as critical as to be mandated for each interation of development.
Instead of completing all bucket requirements each iteration, product teams must complete only one SDL requirement from each bucket of related tasks during each sprint.

* Verification tasks: Attack surface review, negative testing, regular penetration testing
* Design review: Review crypto design and user account control
* Define/update quality gates/bug bar
* Identify functional aspects of the software that require closer review
* Deal with third-party software

# On requirement Changes

The third category of SDL consists of task which must be performed on every requirement change that has consequences on the functional behaviour of the project itself. 

* Security and privacy analysis includes assigning security experts
* Defining minimum security and privacy criteria for the application

The fourth category of SDL requirements consists of tasks that need to be met when you first start a new project or when you first start using SDL with an existing project. These are generally once-per-project tasks that do not need to be repeated after they are complete.

# One-Time Requirements

* Deploying a security vulnerability item tracking system which allows for creation, tracking and reporting of software vulnerabilities

# Third-Party Software

Third-Party software can introduce its own security vulnerabilities and effective countermeasures can only be constructed if one is aware of them. Bugtrackers which exist for most software projects can be used to keep an eye on such vulnerabilities but have to be checked regularly. This can easily grow out of hand if a project accumulates a large number of third-party dependencies and checking each one for security issues can be a an insurmountable task. In such a case it is necessary to select key dependencies and to focus on those.

# Testing

TBD.

# Handling the Bug Bar

TBD (github issue tracker).
